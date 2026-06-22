package dev.foss.obdforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.foss.obdforge.data.local.entity.AuditLogEntity

@Dao
interface AuditLogDao {
    @Insert
    suspend fun insert(entry: AuditLogEntity): Long

    @Query("SELECT COUNT(*) FROM audit_logs")
    suspend fun count(): Int
}
