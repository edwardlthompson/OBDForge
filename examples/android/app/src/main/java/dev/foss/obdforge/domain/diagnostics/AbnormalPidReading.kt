package dev.foss.obdforge.domain.diagnostics

import dev.foss.obdforge.domain.livedata.PidUnit

enum class AbnormalReason {
    BelowMin,
    AboveMax,
}

data class AbnormalPidReading(
    val pid: Int,
    val name: String,
    val formattedValue: String,
    val unit: PidUnit,
    val numericValue: Double,
    val reason: AbnormalReason,
)

data class VehicleHealthSnapshot(
    val dtcs: List<String>,
    val abnormalPids: List<AbnormalPidReading>,
)
