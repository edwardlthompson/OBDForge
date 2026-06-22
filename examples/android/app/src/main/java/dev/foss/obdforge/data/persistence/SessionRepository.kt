package dev.foss.obdforge.data.persistence

import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.local.entity.DtcSnapshotEntity
import dev.foss.obdforge.data.local.entity.FreezeFrameEntity
import dev.foss.obdforge.data.local.entity.SessionEntity
import dev.foss.obdforge.domain.protocol.DtcList
import dev.foss.obdforge.domain.session.DtcSnapshotRecord
import dev.foss.obdforge.domain.session.FreezeFrameRecord
import dev.foss.obdforge.domain.session.SessionDetail
import dev.foss.obdforge.domain.session.SessionSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionRepository(
    private val database: ObdForgeDatabase,
) {
    private val sessionDao = database.sessionDao()
    private val dtcSnapshotDao = database.dtcSnapshotDao()
    private val freezeFrameDao = database.freezeFrameDao()

    fun observeSummaries(): Flow<List<SessionSummary>> =
        database.sessionSummaryQuery().observeSummaries().map { rows ->
            rows.map { row ->
                SessionSummary(
                    id = row.session.id,
                    startedAtEpochMs = row.session.startedAtEpochMs,
                    endedAtEpochMs = row.session.endedAtEpochMs,
                    transportType = row.session.transportType,
                    protocolId = row.session.protocolId,
                    vin = row.session.vin,
                    dtcCount = row.dtcCount,
                )
            }
        }

    suspend fun startSession(
        transportType: String,
        protocolId: String?,
        vin: String?,
        startedAtEpochMs: Long = System.currentTimeMillis(),
    ): Long = sessionDao.insert(
        SessionEntity(
            startedAtEpochMs = startedAtEpochMs,
            transportType = transportType,
            protocolId = protocolId,
            vin = vin,
        ),
    )

    suspend fun endSession(sessionId: Long, endedAtEpochMs: Long = System.currentTimeMillis()) {
        sessionDao.endSession(sessionId, endedAtEpochMs)
    }

    suspend fun saveDtcSnapshot(sessionId: Long, dtcList: DtcList) {
        dtcSnapshotDao.insert(
            DtcSnapshotEntity(
                sessionId = sessionId,
                capturedAtEpochMs = System.currentTimeMillis(),
                codesJson = SessionJsonCodec.encodeCodes(dtcList.entries.map { it.code }),
                rawResponse = dtcList.raw,
            ),
        )
    }

    suspend fun saveFreezeFrame(
        sessionId: Long,
        dtcCode: String,
        pidValues: Map<String, String>,
    ) {
        freezeFrameDao.insert(
            FreezeFrameEntity(
                sessionId = sessionId,
                dtcCode = dtcCode,
                capturedAtEpochMs = System.currentTimeMillis(),
                pidDataJson = SessionJsonCodec.encodePidValues(pidValues),
            ),
        )
    }

    suspend fun getDetail(sessionId: Long): SessionDetail? {
        val session = sessionDao.getById(sessionId) ?: return null
        val dtcSnapshots = dtcSnapshotDao.forSession(sessionId).map { entity ->
            DtcSnapshotRecord(
                capturedAtEpochMs = entity.capturedAtEpochMs,
                codes = SessionJsonCodec.decodeCodes(entity.codesJson),
                rawResponse = entity.rawResponse,
            )
        }
        val freezeFrames = freezeFrameDao.forSession(sessionId).map { entity ->
            FreezeFrameRecord(
                dtcCode = entity.dtcCode,
                capturedAtEpochMs = entity.capturedAtEpochMs,
                pidValues = SessionJsonCodec.decodePidValues(entity.pidDataJson),
            )
        }
        val dtcCount = dtcSnapshots.sumOf { it.codes.size }
        return SessionDetail(
            summary = SessionSummary(
                id = session.id,
                startedAtEpochMs = session.startedAtEpochMs,
                endedAtEpochMs = session.endedAtEpochMs,
                transportType = session.transportType,
                protocolId = session.protocolId,
                vin = session.vin,
                dtcCount = dtcCount,
            ),
            dtcSnapshots = dtcSnapshots,
            freezeFrames = freezeFrames,
        )
    }

    suspend fun exportJson(sessionId: Long): String? =
        getDetail(sessionId)?.let { SessionJsonExporter.export(it) }

    suspend fun exportCsv(sessionId: Long): String? =
        getDetail(sessionId)?.let { SessionCsvExporter.export(it) }
}
