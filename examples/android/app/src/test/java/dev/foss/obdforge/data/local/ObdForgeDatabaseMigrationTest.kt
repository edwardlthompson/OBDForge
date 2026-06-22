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
        val dbName = "migration-test"
        context.deleteDatabase(dbName)

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

        val room = Room.databaseBuilder(context, ObdForgeDatabase::class.java, dbName)
            .addMigrations(ObdForgeDatabase.MIGRATION_1_2)
            .build()
        room.openHelper.writableDatabase.use { migrated ->
            val cursor = migrated.query("PRAGMA table_info(sessions)")
            var found = false
            while (cursor.moveToNext()) {
                if (cursor.getString(1) == "endedAtEpochMs") found = true
            }
            cursor.close()
            assertTrue(found)
        }
        room.close()
    }
}
