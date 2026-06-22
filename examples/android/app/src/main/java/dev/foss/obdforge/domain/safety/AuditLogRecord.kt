package dev.foss.obdforge.domain.safety

data class AuditLogRecord(
    val id: Long,
    val timestampEpochMs: Long,
    val persona: String,
    val protocolId: String?,
    val commandType: String,
    val commandHash: String,
    val outcome: String,
    val userNote: String?,
)
