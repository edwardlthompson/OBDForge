package dev.foss.obdforge.data.ai

object UnavailableLlmEngine : LocalLlmEngine {
    override val isAvailable: Boolean = false

    override suspend fun generate(prompt: String): Result<String> =
        Result.failure(IllegalStateException("local LLM unavailable"))

    override fun close() = Unit
}
