package dev.foss.obdforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.foss.obdforge.data.local.entity.DtcSnapshotEntity

@Dao
interface DtcSnapshotDao {
    @Insert
    suspend fun insert(snapshot: DtcSnapshotEntity): Long

    @Query("SELECT * FROM dtc_snapshots WHERE sessionId = :sessionId ORDER BY capturedAtEpochMs ASC")
    suspend fun forSession(sessionId: Long): List<DtcSnapshotEntity>

    @Query("SELECT COUNT(*) FROM dtc_snapshots WHERE sessionId = :sessionId")
    suspend fun countForSession(sessionId: Long): Int
}
