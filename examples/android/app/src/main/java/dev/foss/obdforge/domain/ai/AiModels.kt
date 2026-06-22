package dev.foss.obdforge.domain.ai

enum class AiExplanationSource {
    Catalog,
    MediaPipe,
}

enum class DtcSeverity {
    Low,
    Medium,
    High,
    Unknown,
}

data class DtcClassification(
    val severity: DtcSeverity,
    val category: String,
    val confidence: Float,
)

data class DtcExplanation(
    val code: String,
    val title: String,
    val summary: String,
    val source: AiExplanationSource,
    val severity: DtcSeverity,
    val classification: DtcClassification?,
)

sealed class ExplainDtcOutcome {
    data class Success(val explanation: DtcExplanation) : ExplainDtcOutcome()
    data object InvalidCode : ExplainDtcOutcome()
    data object Unavailable : ExplainDtcOutcome()
}
