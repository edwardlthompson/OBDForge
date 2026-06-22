package dev.foss.obdforge.data.local

import android.content.Context
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
        ObdForgeDatabaseMigrationFixtures.createV1Schema(context, dbName)

        val room = ObdForgeDatabaseMigrationFixtures.buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(ObdForgeDatabaseMigrationFixtures.hasColumn(migrated, "sessions", "endedAtEpochMs"))
        }
        room.close()
    }

    @Test
    fun migrate2To3_addsDtcAndFreezeTables() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration-test-2-3"
        context.deleteDatabase(dbName)
        ObdForgeDatabaseMigrationFixtures.createV1Schema(context, dbName)
        ObdForgeDatabaseMigrationFixtures.bumpToVersion2(context, dbName)

        val room = ObdForgeDatabaseMigrationFixtures.buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(ObdForgeDatabaseMigrationFixtures.tableExists(migrated, "dtc_snapshots"))
            assertTrue(ObdForgeDatabaseMigrationFixtures.tableExists(migrated, "freeze_frames"))
        }
        room.close()
    }

    @Test
    fun migrate3To4_expandsAuditLogColumns() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration-test-3-4"
        context.deleteDatabase(dbName)
        ObdForgeDatabaseMigrationFixtures.createV1Schema(context, dbName)
        ObdForgeDatabaseMigrationFixtures.bumpToVersion2(context, dbName)

        val room = ObdForgeDatabaseMigrationFixtures.buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(ObdForgeDatabaseMigrationFixtures.hasColumn(migrated, "audit_logs", "persona"))
            assertTrue(ObdForgeDatabaseMigrationFixtures.hasColumn(migrated, "audit_logs", "commandHash"))
            assertTrue(ObdForgeDatabaseMigrationFixtures.hasColumn(migrated, "audit_logs", "commandType"))
        }
        room.close()
    }

    @Test
    fun migrate4To5_addsVehicleProfilesTable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration-test-4-5"
        context.deleteDatabase(dbName)
        ObdForgeDatabaseMigrationFixtures.createV1Schema(context, dbName)
        ObdForgeDatabaseMigrationFixtures.bumpToVersion2(context, dbName)

        val room = ObdForgeDatabaseMigrationFixtures.buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(ObdForgeDatabaseMigrationFixtures.tableExists(migrated, "vehicle_profiles"))
            assertTrue(ObdForgeDatabaseMigrationFixtures.hasColumn(migrated, "vehicle_profiles", "sourceType"))
        }
        room.close()
    }

    @Test
    fun migrate5To6_addsShopTables() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration-test-5-6"
        context.deleteDatabase(dbName)
        ObdForgeDatabaseMigrationFixtures.createV1Schema(context, dbName)
        ObdForgeDatabaseMigrationFixtures.bumpToVersion2(context, dbName)

        val room = ObdForgeDatabaseMigrationFixtures.buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(ObdForgeDatabaseMigrationFixtures.tableExists(migrated, "customers"))
            assertTrue(ObdForgeDatabaseMigrationFixtures.tableExists(migrated, "work_orders"))
        }
        room.close()
    }

    @Test
    fun migrate6To7_addsDiagnosticEventsTable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration-test-6-7"
        context.deleteDatabase(dbName)
        ObdForgeDatabaseMigrationFixtures.createV1Schema(context, dbName)
        ObdForgeDatabaseMigrationFixtures.bumpToVersion2(context, dbName)

        val room = ObdForgeDatabaseMigrationFixtures.buildRoom(context, dbName)
        room.openHelper.writableDatabase.use { migrated ->
            assertTrue(ObdForgeDatabaseMigrationFixtures.tableExists(migrated, "diagnostic_events"))
            assertTrue(
                ObdForgeDatabaseMigrationFixtures.hasColumn(migrated, "diagnostic_events", "category"),
            )
        }
        room.close()
    }
}
