package dev.foss.obdforge.data.persistence

import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.local.entity.AuditLogEntity
import dev.foss.obdforge.domain.safety.AuditLogRecord

class AuditLogRepository(
    private val database: ObdForgeDatabase,
) {
    private val dao = database.auditLogDao()

    suspend fun recordAttempt(
        persona: String,
        protocolId: String?,
        commandType: String,
        commandHash: String,
        outcome: String,
        userNote: String? = null,
        timestampEpochMs: Long = System.currentTimeMillis(),
    ): Long = dao.insert(
        AuditLogEntity(
            timestampEpochMs = timestampEpochMs,
            persona = persona,
            protocolId = protocolId,
            commandType = commandType,
            commandHash = commandHash,
            outcome = outcome,
            userNote = userNote,
        ),
    )

    suspend fun allRecords(): List<AuditLogRecord> =
        dao.all().map { entity -> entity.toRecord() }

    suspend fun exportJson(): String = AuditLogJsonExporter.export(allRecords())

    suspend fun purgeOlderThan(beforeEpochMs: Long): Int = dao.deleteOlderThan(beforeEpochMs)

    suspend fun count(): Int = dao.count()

    private fun AuditLogEntity.toRecord() = AuditLogRecord(
        id = id,
        timestampEpochMs = timestampEpochMs,
        persona = persona,
        protocolId = protocolId,
        commandType = commandType,
        commandHash = commandHash,
        outcome = outcome,
        userNote = userNote,
    )
}
