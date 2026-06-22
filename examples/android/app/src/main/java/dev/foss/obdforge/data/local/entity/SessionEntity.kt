package dev.foss.obdforge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAtEpochMs: Long,
    val endedAtEpochMs: Long? = null,
    val transportType: String,
    val protocolId: String?,
    val vin: String?,
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestampEpochMs: Long,
    val persona: String,
    val protocolId: String?,
    val commandType: String,
    val commandHash: String,
    val outcome: String,
    val userNote: String?,
)
