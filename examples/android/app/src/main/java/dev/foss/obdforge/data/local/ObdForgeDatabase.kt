package dev.foss.obdforge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.foss.obdforge.data.local.dao.AuditLogDao
import dev.foss.obdforge.data.local.dao.DtcSnapshotDao
import dev.foss.obdforge.data.local.dao.FreezeFrameDao
import dev.foss.obdforge.data.local.dao.SessionDao
import dev.foss.obdforge.data.local.dao.SessionSummaryQuery
import dev.foss.obdforge.data.local.entity.AuditLogEntity
import dev.foss.obdforge.data.local.entity.DtcSnapshotEntity
import dev.foss.obdforge.data.local.entity.FreezeFrameEntity
import dev.foss.obdforge.data.local.entity.SessionEntity

@Database(
    entities = [
        SessionEntity::class,
        AuditLogEntity::class,
        DtcSnapshotEntity::class,
        FreezeFrameEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class ObdForgeDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun sessionSummaryQuery(): SessionSummaryQuery
    abstract fun auditLogDao(): AuditLogDao
    abstract fun dtcSnapshotDao(): DtcSnapshotDao
    abstract fun freezeFrameDao(): FreezeFrameDao

    companion object {
        const val DB_NAME = "obdforge.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sessions ADD COLUMN endedAtEpochMs INTEGER")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS dtc_snapshots (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sessionId INTEGER NOT NULL,
                        capturedAtEpochMs INTEGER NOT NULL,
                        codesJson TEXT NOT NULL,
                        rawResponse TEXT NOT NULL,
                        FOREIGN KEY(sessionId) REFERENCES sessions(id) ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_dtc_snapshots_sessionId ON dtc_snapshots(sessionId)",
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS freeze_frames (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sessionId INTEGER NOT NULL,
                        dtcCode TEXT NOT NULL,
                        capturedAtEpochMs INTEGER NOT NULL,
                        pidDataJson TEXT NOT NULL,
                        FOREIGN KEY(sessionId) REFERENCES sessions(id) ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_freeze_frames_sessionId ON freeze_frames(sessionId)",
                )
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS audit_logs_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestampEpochMs INTEGER NOT NULL,
                        persona TEXT NOT NULL,
                        protocolId TEXT,
                        commandType TEXT NOT NULL,
                        commandHash TEXT NOT NULL,
                        outcome TEXT NOT NULL,
                        userNote TEXT
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    INSERT INTO audit_logs_new (
                        id, timestampEpochMs, persona, protocolId, commandType, commandHash, outcome, userNote
                    )
                    SELECT id, timestampEpochMs, 'Diy', NULL, action, '', outcome, detail
                    FROM audit_logs
                    """.trimIndent(),
                )
                db.execSQL("DROP TABLE audit_logs")
                db.execSQL("ALTER TABLE audit_logs_new RENAME TO audit_logs")
            }
        }
    }
}
