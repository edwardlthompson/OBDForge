package dev.foss.obdforge.domain.session

data class SessionSummary(
    val id: Long,
    val startedAtEpochMs: Long,
    val endedAtEpochMs: Long?,
    val transportType: String,
    val protocolId: String?,
    val vin: String?,
    val dtcCount: Int,
)

data class DtcSnapshotRecord(
    val capturedAtEpochMs: Long,
    val codes: List<String>,
    val rawResponse: String,
)

data class FreezeFrameRecord(
    val dtcCode: String,
    val capturedAtEpochMs: Long,
    val pidValues: Map<String, String>,
)

data class SessionDetail(
    val summary: SessionSummary,
    val dtcSnapshots: List<DtcSnapshotRecord>,
    val freezeFrames: List<FreezeFrameRecord>,
)
