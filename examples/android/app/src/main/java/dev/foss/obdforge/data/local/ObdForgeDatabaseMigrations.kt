package dev.foss.obdforge.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object ObdForgeDatabaseMigrations {
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

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS vehicle_profiles (
                    vin TEXT NOT NULL PRIMARY KEY,
                    sourceType TEXT NOT NULL,
                    resolvedAtEpochMs INTEGER NOT NULL,
                    adapterIdHash TEXT,
                    label TEXT
                )
                """.trimIndent(),
            )
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS customers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    phone TEXT,
                    email TEXT,
                    createdAtEpochMs INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS work_orders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    customerId INTEGER,
                    vin TEXT NOT NULL,
                    status TEXT NOT NULL,
                    notes TEXT,
                    sessionId INTEGER,
                    openedAtEpochMs INTEGER NOT NULL,
                    closedAtEpochMs INTEGER,
                    FOREIGN KEY(customerId) REFERENCES customers(id) ON DELETE SET NULL,
                    FOREIGN KEY(sessionId) REFERENCES sessions(id) ON DELETE SET NULL
                )
                """.trimIndent(),
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_work_orders_customerId ON work_orders(customerId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_work_orders_sessionId ON work_orders(sessionId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_work_orders_status ON work_orders(status)")
        }
    }
}
