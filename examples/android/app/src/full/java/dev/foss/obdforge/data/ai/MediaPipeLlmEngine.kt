package dev.foss.obdforge.data.ai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaPipeLlmEngine(
    private val context: Context,
) : LocalLlmEngine {
    private var session: MediaPipeLlmSession? = null

    override val isAvailable: Boolean
        get() = LlmModelProvisioner.isModelPresent(context)

    override suspend fun generate(prompt: String): Result<String> = withContext(Dispatchers.Default) {
        runCatching {
            val engine = session ?: MediaPipeLlmSession.create(context).also { session = it }
            engine.generate(prompt)
        }
    }

    override fun close() {
        session?.close()
        session = null
    }
}
