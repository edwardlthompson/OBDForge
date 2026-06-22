package dev.foss.obdforge.data.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class MediaPipeLlmEngine(
    private val context: Context,
) : LocalLlmEngine {
    private var inference: LlmInference? = null

    override val isAvailable: Boolean
        get() = resolveModelPath(context) != null

    override suspend fun generate(prompt: String): Result<String> = withContext(Dispatchers.Default) {
        runCatching {
            val engine = inference ?: createInference().also { inference = it }
            engine.generateResponse(prompt).trim()
        }
    }

    override fun close() {
        inference?.close()
        inference = null
    }

    private fun createInference(): LlmInference {
        val modelPath = requireNotNull(resolveModelPath(context)) { "LLM model asset missing" }
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(modelPath)
            .setMaxTokens(256)
            .build()
        return LlmInference.createFromOptions(context, options)
    }

    companion object {
        const val MODEL_ASSET = "ai/llm_model.task"

        fun resolveModelPath(context: Context): String? {
            return try {
                context.assets.open(MODEL_ASSET).close()
                val outFile = File(context.filesDir, "llm_model.task")
                if (!outFile.exists()) {
                    context.assets.open(MODEL_ASSET).use { input ->
                        outFile.outputStream().use { output -> input.copyTo(output) }
                    }
                }
                outFile.absolutePath
            } catch (_: IOException) {
                null
            }
        }
    }
}
