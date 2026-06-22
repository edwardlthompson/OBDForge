package dev.foss.obdforge.domain.livedata

data class LivePidSample(
    val pid: Int,
    val name: String,
    val formattedValue: String,
    val unit: PidUnit,
    val numericValue: Double?,
    val updatedAtMs: Long,
)

data class LiveDataSnapshot(
    val samples: Map<Int, LivePidSample>,
    val sequence: Long,
)
