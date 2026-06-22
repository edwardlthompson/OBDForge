package dev.foss.obdforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.foss.obdforge.data.local.entity.DiagnosticEventEntity

@Dao
interface DiagnosticEventDao {
    @Insert
    suspend fun insert(entry: DiagnosticEventEntity): Long

    @Query("SELECT * FROM diagnostic_events ORDER BY timestampEpochMs DESC")
    suspend fun all(): List<DiagnosticEventEntity>

    @Query("SELECT COUNT(*) FROM diagnostic_events")
    suspend fun count(): Int

    @Query("DELETE FROM diagnostic_events WHERE timestampEpochMs < :beforeEpochMs")
    suspend fun deleteOlderThan(beforeEpochMs: Long): Int

    @Query(
        """
        DELETE FROM diagnostic_events WHERE id IN (
            SELECT id FROM diagnostic_events ORDER BY timestampEpochMs ASC LIMIT :count
        )
        """,
    )
    suspend fun deleteOldest(count: Int): Int
}
