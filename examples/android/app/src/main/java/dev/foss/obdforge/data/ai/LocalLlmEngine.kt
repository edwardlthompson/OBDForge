package dev.foss.obdforge.data.ai

interface LocalLlmEngine {
    val isAvailable: Boolean
    suspend fun generate(prompt: String): Result<String>
    fun close()
}
