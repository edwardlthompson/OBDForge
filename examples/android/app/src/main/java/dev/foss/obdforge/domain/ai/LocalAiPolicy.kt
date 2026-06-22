package dev.foss.obdforge.domain.ai

import dev.foss.obdforge.domain.livedata.PersonaMode

object LocalAiPolicy {
    private val DTC_PATTERN = Regex("^[PCBU][0-9]{4}$")

    fun normalizeCode(input: String): String? {
        val code = input.uppercase().replace(" ", "")
        return code.takeIf { DTC_PATTERN.matches(it) }
    }

    fun buildLlmPrompt(
        code: String,
        persona: PersonaMode,
        catalogHint: DtcCatalog.CatalogEntry?,
        classification: DtcClassification?,
    ): String {
        val style = when (persona) {
            PersonaMode.Diy -> "Explain in plain language for a vehicle owner."
            PersonaMode.SemiPro -> "Explain for an enthusiast with moderate technical detail."
            PersonaMode.Shop -> "Provide a concise technician summary with suggested checks."
            PersonaMode.Racing -> "Keep the answer brief and action-oriented."
        }
        val hint = catalogHint?.title ?: "Unknown code"
        val severity = classification?.severity?.name ?: catalogHint?.severity?.name ?: "Unknown"
        return buildString {
            appendLine("You are an on-device OBD diagnostic assistant. Do not use the internet.")
            appendLine(style)
            appendLine("DTC: $code ($hint)")
            appendLine("Severity hint: $severity")
            append("Give a short explanation and one practical next step.")
        }
    }

    fun formatCatalogSummary(
        entry: DtcCatalog.CatalogEntry,
        persona: PersonaMode,
        classification: DtcClassification?,
    ): String {
        val prefix = when (persona) {
            PersonaMode.Diy -> entry.summary
            PersonaMode.Shop -> "${entry.summary} Suggested category: ${entry.category}."
            else -> entry.summary
        }
        val severityNote = classification?.let { " Classifier severity: ${it.severity.name}." }.orEmpty()
        return prefix + severityNote
    }
}
