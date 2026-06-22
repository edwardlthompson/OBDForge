package dev.foss.obdforge.data.ai

import dev.foss.obdforge.domain.ai.AiExplanationSource
import dev.foss.obdforge.domain.ai.DtcCatalog
import dev.foss.obdforge.domain.ai.DtcExplanation
import dev.foss.obdforge.domain.ai.DtcSeverity
import dev.foss.obdforge.domain.ai.ExplainDtcOutcome
import dev.foss.obdforge.domain.ai.LocalAiPolicy
import dev.foss.obdforge.domain.livedata.PersonaMode

class ExplainDtcUseCase(
    private val llmEngine: LocalLlmEngine,
    private val classifier: DtcClassifier,
) {
    suspend fun explain(
        code: String,
        persona: PersonaMode,
        manufacturer: String? = null,
    ): ExplainDtcOutcome {
        val normalized = LocalAiPolicy.normalizeCode(code) ?: return ExplainDtcOutcome.InvalidCode
        val catalogEntry = DtcCatalog.lookup(normalized, manufacturer)
        val classification = classifier.takeIf { it.isAvailable }?.classify(normalized)

        if (llmEngine.isAvailable) {
            val prompt = LocalAiPolicy.buildLlmPrompt(normalized, persona, catalogEntry, classification)
            llmEngine.generate(prompt).getOrNull()?.takeIf { it.isNotBlank() }?.let { summary ->
                return ExplainDtcOutcome.Success(
                    DtcExplanation(
                        code = normalized,
                        title = catalogEntry?.title ?: "DTC $normalized",
                        summary = summary,
                        source = AiExplanationSource.MediaPipe,
                        severity = classification?.severity ?: catalogEntry?.severity ?: DtcSeverity.Unknown,
                        classification = classification,
                    ),
                )
            }
        }

        val catalog = DtcCatalog.explain(normalized, manufacturer)
            ?: return ExplainDtcOutcome.Unavailable
        val summary = catalogEntry?.let {
            LocalAiPolicy.formatCatalogSummary(it, persona, classification)
        } ?: catalog.summary
        return ExplainDtcOutcome.Success(
            catalog.copy(
                summary = summary,
                classification = classification ?: catalog.classification,
                severity = classification?.severity ?: catalog.severity,
            ),
        )
    }
}
