package dev.foss.obdforge.data.ai

import android.content.Context

object LocalLlmEngineFactory {
    fun create(context: Context): LocalLlmEngine =
        MediaPipeLlmEngine(context.applicationContext)
}
