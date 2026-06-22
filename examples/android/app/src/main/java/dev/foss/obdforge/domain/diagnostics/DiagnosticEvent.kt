package dev.foss.obdforge.domain.diagnostics

data class DiagnosticEventRecord(
    val id: Long,
    val timestampEpochMs: Long,
    val category: DiagnosticEventCategory,
    val severity: DiagnosticEventSeverity,
    val transportType: String?,
    val protocolId: String?,
    val message: String,
    val detail: String?,
)

enum class DiagnosticEventCategory {
    Connection,
    Protocol,
    TransportIo,
    Crash,
    App,
}

enum class DiagnosticEventSeverity {
    Info,
    Warn,
    Error,
}
