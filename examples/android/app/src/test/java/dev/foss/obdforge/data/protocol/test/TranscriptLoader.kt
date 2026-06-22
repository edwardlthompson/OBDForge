package dev.foss.obdforge.data.protocol.test

data class TranscriptStep(
    val command: String,
    val response: String,
)

object TranscriptLoader {
    fun parse(text: String): List<TranscriptStep> {
        val steps = mutableListOf<TranscriptStep>()
        var pendingCommand: String? = null
        text.lineSequence().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEach
            when {
                trimmed.startsWith(">") -> pendingCommand = trimmed.removePrefix(">").trim()
                trimmed.startsWith("<") -> {
                    val command = pendingCommand
                        ?: error("Transcript response without command: $trimmed")
                    steps.add(TranscriptStep(command, trimmed.removePrefix("<").trim()))
                    pendingCommand = null
                }
            }
        }
        return steps
    }

    fun loadClasspath(resourcePath: String): List<TranscriptStep> {
        val stream = checkNotNull(javaClass.classLoader.getResourceAsStream(resourcePath)) {
            "Missing transcript resource: $resourcePath"
        }
        return stream.bufferedReader().use { parse(it.readText()) }
    }
}
