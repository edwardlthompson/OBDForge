package dev.foss.obdforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.foss.obdforge.data.local.entity.FreezeFrameEntity

@Dao
interface FreezeFrameDao {
    @Insert
    suspend fun insert(frame: FreezeFrameEntity): Long

    @Query("SELECT * FROM freeze_frames WHERE sessionId = :sessionId ORDER BY capturedAtEpochMs ASC")
    suspend fun forSession(sessionId: Long): List<FreezeFrameEntity>
}
