package dev.foss.obdforge.domain.vehicle

data class VehicleProfile(
    val vin: String,
    val source: VinSourceType,
    val resolvedAtEpochMs: Long,
    val adapterIdHash: String?,
    val label: String?,
)
