package dev.foss.obdforge.data.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dev.foss.obdforge.domain.ai.LocalAiConfig

/** Loaded only when generate() runs so unit tests never open MediaPipe JNI. */
internal class MediaPipeLlmSession(
    private val inference: LlmInference,
) : AutoCloseable {
    fun generate(prompt: String): String = inference.generateResponse(prompt).trim()

    override fun close() = inference.close()

    companion object {
        fun create(context: Context): MediaPipeLlmSession {
            val modelPath = requireNotNull(LlmModelProvisioner.resolveModelPath(context)) {
                "LLM model missing"
            }
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(LocalAiConfig.MAX_OUTPUT_TOKENS)
                .setPreferredBackend(LlmInference.Backend.CPU)
                .build()
            return MediaPipeLlmSession(LlmInference.createFromOptions(context, options))
        }
    }
}
