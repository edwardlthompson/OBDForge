package dev.foss.obdforge.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diagnostic_events",
    indices = [Index(value = ["timestampEpochMs"])],
)
data class DiagnosticEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestampEpochMs: Long,
    val category: String,
    val severity: String,
    val transportType: String?,
    val protocolId: String?,
    val message: String,
    val detail: String?,
)
