package dev.foss.obdforge.data.diagnostics

object DiagnosticLogSanitizer {
    private val vinPattern = Regex("""\b[A-HJ-NPR-Z0-9]{17}\b""", RegexOption.IGNORE_CASE)
    private val macPattern = Regex("""\b([0-9A-F]{2}:){5}[0-9A-F]{2}\b""", RegexOption.IGNORE_CASE)

    fun sanitize(text: String): String =
        macPattern.replace(vinPattern.replace(text, "[VIN]"), "[MAC]")

    fun stackTrace(throwable: Throwable): String =
        sanitize(throwable.stackTraceToString().take(MAX_STACK_CHARS))

    private const val MAX_STACK_CHARS = 8_000
}
