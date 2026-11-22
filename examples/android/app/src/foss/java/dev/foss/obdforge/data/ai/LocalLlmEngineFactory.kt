package dev.foss.obdforge.data.ai

import android.content.Context

object LocalLlmEngineFactory {
    @Suppress("UNUSED_PARAMETER")
    fun create(context: Context): LocalLlmEngine = UnavailableLlmEngine
}
