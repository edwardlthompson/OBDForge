package dev.foss.obdforge.data.persistence

import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.local.entity.DiagnosticEventEntity
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventCategory
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventRecord
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventSeverity

class DiagnosticEventRepository(
    private val database: ObdForgeDatabase,
) {
    private val dao = database.diagnosticEventDao()

    suspend fun record(
        category: DiagnosticEventCategory,
        severity: DiagnosticEventSeverity,
        message: String,
        transportType: String? = null,
        protocolId: String? = null,
        detail: String? = null,
        timestampEpochMs: Long = System.currentTimeMillis(),
    ): Long {
        enforceRetentionCap()
        return dao.insert(
            DiagnosticEventEntity(
                timestampEpochMs = timestampEpochMs,
                category = category.name,
                severity = severity.name,
                transportType = transportType,
                protocolId = protocolId,
                message = message,
                detail = detail,
            ),
        )
    }

    suspend fun allRecords(): List<DiagnosticEventRecord> =
        dao.all().map { entity -> entity.toRecord() }

    suspend fun exportJson(): String = DiagnosticEventJsonExporter.export(allRecords())

    suspend fun purgeOlderThan(beforeEpochMs: Long): Int = dao.deleteOlderThan(beforeEpochMs)

    suspend fun count(): Int = dao.count()

    private suspend fun enforceRetentionCap() {
        val overflow = dao.count() - MAX_ENTRIES + 1
        if (overflow > 0) {
            dao.deleteOldest(overflow)
        }
    }

    private fun DiagnosticEventEntity.toRecord() = DiagnosticEventRecord(
        id = id,
        timestampEpochMs = timestampEpochMs,
        category = DiagnosticEventCategory.valueOf(category),
        severity = DiagnosticEventSeverity.valueOf(severity),
        transportType = transportType,
        protocolId = protocolId,
        message = message,
        detail = detail,
    )

    companion object {
        const val MAX_ENTRIES = 2_000
        const val RETENTION_DAYS = 30
    }
}
