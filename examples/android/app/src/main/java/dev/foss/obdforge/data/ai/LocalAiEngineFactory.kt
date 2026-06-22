package dev.foss.obdforge.data.ai

import android.content.Context

object LocalAiEngineFactory {
    fun createExplainDtcUseCase(context: Context): ExplainDtcUseCase {
        val llmEngine = MediaPipeLlmEngine(context.applicationContext)
        val classifier = TfliteDtcClassifier(context.applicationContext)
            .takeIf { it.isAvailable } ?: NoOpDtcClassifier
        return ExplainDtcUseCase(
            llmEngine = llmEngine,
            classifier = classifier,
        )
    }
}
