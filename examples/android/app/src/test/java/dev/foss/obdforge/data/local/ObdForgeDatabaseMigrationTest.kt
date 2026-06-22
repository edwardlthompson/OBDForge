package dev.foss.obdforge.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ObdForgeDatabaseMigrationTest {
    @Test
    fun migrate1To2_addsEndedAtColumn() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration-test-1-2"
        context.deleteDatabase(dbName)
        createV1Schema(context, dbName)

        val room = buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(hasColumn(migrated, "sessions", "endedAtEpochMs"))
        }
        room.close()
    }

    @Test
    fun migrate2To3_addsDtcAndFreezeTables() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration-test-2-3"
        context.deleteDatabase(dbName)
        createV1Schema(context, dbName)
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(dbName).absolutePath, null).use {
            it.execSQL("ALTER TABLE sessions ADD COLUMN endedAtEpochMs INTEGER")
            it.version = 2
        }

        val room = buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(tableExists(migrated, "dtc_snapshots"))
            assertTrue(tableExists(migrated, "freeze_frames"))
        }
        room.close()
    }

    @Test
    fun migrate3To4_expandsAuditLogColumns() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration-test-3-4"
        context.deleteDatabase(dbName)
        createV1Schema(context, dbName)
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(dbName).absolutePath, null).use {
            it.execSQL("ALTER TABLE sessions ADD COLUMN endedAtEpochMs INTEGER")
            it.version = 2
        }

        val room = buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(hasColumn(migrated, "audit_logs", "persona"))
            assertTrue(hasColumn(migrated, "audit_logs", "commandHash"))
            assertTrue(hasColumn(migrated, "audit_logs", "commandType"))
        }
        room.close()
    }

    @Test
    fun migrate4To5_addsVehicleProfilesTable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration-test-4-5"
        context.deleteDatabase(dbName)
        createV1Schema(context, dbName)
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(dbName).absolutePath, null).use {
            it.execSQL("ALTER TABLE sessions ADD COLUMN endedAtEpochMs INTEGER")
            it.version = 2
        }

        val room = buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(tableExists(migrated, "vehicle_profiles"))
            assertTrue(hasColumn(migrated, "vehicle_profiles", "sourceType"))
        }
        room.close()
    }

    @Test
    fun migrate5To6_addsShopTables() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration-test-5-6"
        context.deleteDatabase(dbName)
        createV1Schema(context, dbName)
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(dbName).absolutePath, null).use {
            it.execSQL("ALTER TABLE sessions ADD COLUMN endedAtEpochMs INTEGER")
            it.version = 2
        }

        val room = buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(tableExists(migrated, "customers"))
            assertTrue(tableExists(migrated, "work_orders"))
        }
        room.close()
    }

    private fun buildRoom(context: Context, dbName: String) =
        Room.databaseBuilder(context, ObdForgeDatabase::class.java, dbName)
            .addMigrations(
                ObdForgeDatabase.MIGRATION_1_2,
                ObdForgeDatabase.MIGRATION_2_3,
                ObdForgeDatabase.MIGRATION_3_4,
                ObdForgeDatabase.MIGRATION_4_5,
                ObdForgeDatabase.MIGRATION_5_6,
            )
            .build()

    private fun createV1Schema(context: Context, dbName: String) {
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

    private fun hasColumn(db: androidx.sqlite.db.SupportSQLiteDatabase, table: String, column: String): Boolean {
        db.query("PRAGMA table_info($table)").use { cursor ->
            while (cursor.moveToNext()) {
                if (cursor.getString(1) == column) return true
            }
        }
        return false
    }

    private fun tableExists(db: androidx.sqlite.db.SupportSQLiteDatabase, table: String): Boolean {
        db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='$table'").use { cursor ->
            return cursor.moveToFirst()
        }
    }
}
