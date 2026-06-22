package dev.foss.obdforge.data.transport

object ObdLineCodec {
    fun encode(line: String): ByteArray = "$line\r".toByteArray(Charsets.US_ASCII)

    fun drainLines(buffer: StringBuilder, chunk: String): List<String> {
        buffer.append(chunk)
        val lines = mutableListOf<String>()
        while (true) {
            val index = buffer.indexOf("\r")
            if (index < 0) break
            val line = buffer.substring(0, index).trim()
            buffer.delete(0, index + 1)
            if (line.isNotEmpty()) {
                lines.add(line)
            }
        }
        return lines
    }

    fun responseFromLines(lines: List<String>): String =
        lines.filter { it != ">" && it.isNotBlank() }.joinToString("\n").ifBlank { lines.lastOrNull() ?: "" }
}
