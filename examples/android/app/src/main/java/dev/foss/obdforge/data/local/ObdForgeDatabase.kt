package dev.foss.obdforge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.foss.obdforge.data.local.dao.AuditLogDao
import dev.foss.obdforge.data.local.dao.SessionDao
import dev.foss.obdforge.data.local.entity.AuditLogEntity
import dev.foss.obdforge.data.local.entity.SessionEntity

@Database(
    entities = [SessionEntity::class, AuditLogEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class ObdForgeDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        const val DB_NAME = "obdforge.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sessions ADD COLUMN endedAtEpochMs INTEGER")
            }
        }
    }
}
