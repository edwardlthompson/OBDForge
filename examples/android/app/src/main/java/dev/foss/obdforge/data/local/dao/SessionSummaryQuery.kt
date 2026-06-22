package dev.foss.obdforge.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import dev.foss.obdforge.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

data class SessionSummaryRow(
    @Embedded val session: SessionEntity,
    @ColumnInfo(name = "dtcCount") val dtcCount: Int,
)

@Dao
interface SessionSummaryQuery {
    @Query(
        """
        SELECT sessions.*,
            (SELECT COUNT(*) FROM dtc_snapshots WHERE sessionId = sessions.id) AS dtcCount
        FROM sessions
        ORDER BY startedAtEpochMs DESC
        """,
    )
    fun observeSummaries(): Flow<List<SessionSummaryRow>>
}
