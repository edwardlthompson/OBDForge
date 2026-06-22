package dev.foss.obdforge.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase

internal object ObdForgeDatabaseMigrationFixtures {
    fun buildRoom(context: Context, dbName: String) =
        Room.databaseBuilder(context, ObdForgeDatabase::class.java, dbName)
            .addMigrations(
                ObdForgeDatabase.MIGRATION_1_2,
                ObdForgeDatabase.MIGRATION_2_3,
                ObdForgeDatabase.MIGRATION_3_4,
                ObdForgeDatabase.MIGRATION_4_5,
                ObdForgeDatabase.MIGRATION_5_6,
            )
            .build()

    fun createV1Schema(context: Context, dbName: String) {
        val file = context.getDatabasePath(dbName)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file.absolutePath, null).use { sqlite ->
            sqlite.execSQL(
                """
                CREATE TABLE sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    startedAtEpochMs INTEGER NOT NULL,
                    transportType TEXT NOT NULL,
                    protocolId TEXT,
                    vin TEXT
                )
                """.trimIndent(),
            )
            sqlite.execSQL(
                """
                CREATE TABLE audit_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    timestampEpochMs INTEGER NOT NULL,
                    action TEXT NOT NULL,
                    outcome TEXT NOT NULL,
                    detail TEXT
                )
                """.trimIndent(),
            )
            sqlite.version = 1
        }
    }

    fun bumpToVersion2(context: Context, dbName: String) {
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(dbName).absolutePath, null).use {
            it.execSQL("ALTER TABLE sessions ADD COLUMN endedAtEpochMs INTEGER")
            it.version = 2
        }
    }

    fun hasColumn(db: SupportSQLiteDatabase, table: String, column: String): Boolean {
        db.query("PRAGMA table_info($table)").use { cursor ->
            while (cursor.moveToNext()) {
                if (cursor.getString(1) == column) return true
            }
        }
        return false
    }

    fun tableExists(db: SupportSQLiteDatabase, table: String): Boolean {
        db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='$table'").use { cursor ->
            return cursor.moveToFirst()
        }
    }
}
