package dev.foss.obdforge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_profiles")
data class VehicleProfileEntity(
    @PrimaryKey val vin: String,
    val sourceType: String,
    val resolvedAtEpochMs: Long,
    val adapterIdHash: String?,
    val label: String? = null,
)
