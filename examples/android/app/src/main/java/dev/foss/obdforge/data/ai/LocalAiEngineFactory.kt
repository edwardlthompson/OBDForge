package dev.foss.obdforge.data.ai

import android.content.Context

object LocalAiEngineFactory {
    fun createExplainDtcUseCase(context: Context): ExplainDtcUseCase {
        val classifier = TfliteDtcClassifier(context.applicationContext)
            .takeIf { it.isAvailable } ?: NoOpDtcClassifier
        return ExplainDtcUseCase(
            llmEngine = LocalLlmEngineFactory.create(context),
            classifier = classifier,
        )
    }
}
