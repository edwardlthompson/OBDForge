package dev.foss.obdforge.data.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dev.foss.obdforge.domain.ai.LocalAiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaPipeLlmEngine(
    private val context: Context,
) : LocalLlmEngine {
    private var inference: LlmInference? = null

    override val isAvailable: Boolean
        get() = LlmModelProvisioner.isModelPresent(context)

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
        val modelPath = requireNotNull(LlmModelProvisioner.resolveModelPath(context)) {
            "LLM model missing"
        }
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(modelPath)
            .setMaxTokens(LocalAiConfig.MAX_OUTPUT_TOKENS)
            .setPreferredBackend(LlmInference.Backend.CPU)
            .build()
        return LlmInference.createFromOptions(context, options)
    }

    companion object {
        fun resolveModelPath(context: Context): String? = LlmModelProvisioner.resolveModelPath(context)
    }
}
