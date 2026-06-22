package dev.foss.obdforge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.foss.obdforge.data.local.dao.AuditLogDao
import dev.foss.obdforge.data.local.dao.CustomerDao
import dev.foss.obdforge.data.local.dao.DtcSnapshotDao
import dev.foss.obdforge.data.local.dao.FreezeFrameDao
import dev.foss.obdforge.data.local.dao.SessionDao
import dev.foss.obdforge.data.local.dao.SessionSummaryQuery
import dev.foss.obdforge.data.local.dao.VehicleProfileDao
import dev.foss.obdforge.data.local.dao.WorkOrderDao
import dev.foss.obdforge.data.local.entity.AuditLogEntity
import dev.foss.obdforge.data.local.entity.CustomerEntity
import dev.foss.obdforge.data.local.entity.DtcSnapshotEntity
import dev.foss.obdforge.data.local.entity.FreezeFrameEntity
import dev.foss.obdforge.data.local.entity.SessionEntity
import dev.foss.obdforge.data.local.entity.VehicleProfileEntity
import dev.foss.obdforge.data.local.entity.WorkOrderEntity

@Database(
    entities = [
        SessionEntity::class,
        AuditLogEntity::class,
        DtcSnapshotEntity::class,
        FreezeFrameEntity::class,
        VehicleProfileEntity::class,
        CustomerEntity::class,
        WorkOrderEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
abstract class ObdForgeDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun sessionSummaryQuery(): SessionSummaryQuery
    abstract fun auditLogDao(): AuditLogDao
    abstract fun dtcSnapshotDao(): DtcSnapshotDao
    abstract fun freezeFrameDao(): FreezeFrameDao
    abstract fun vehicleProfileDao(): VehicleProfileDao
    abstract fun customerDao(): CustomerDao
    abstract fun workOrderDao(): WorkOrderDao

    companion object {
        const val DB_NAME = "obdforge.db"

        val MIGRATION_1_2 get() = ObdForgeDatabaseMigrations.MIGRATION_1_2
        val MIGRATION_2_3 get() = ObdForgeDatabaseMigrations.MIGRATION_2_3
        val MIGRATION_3_4 get() = ObdForgeDatabaseMigrations.MIGRATION_3_4
        val MIGRATION_4_5 get() = ObdForgeDatabaseMigrations.MIGRATION_4_5
        val MIGRATION_5_6 get() = ObdForgeDatabaseMigrations.MIGRATION_5_6
    }
}
