package dev.foss.obdforge.data

import android.content.Context
import androidx.room.Room
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry

data class ObdForgeCompositionRoot(
    val transportRegistry: TransportRegistry,
    val protocolRegistry: ProtocolRegistry,
    val database: ObdForgeDatabase,
) {
    companion object {
        fun create(context: Context): ObdForgeCompositionRoot {
            val database = Room.databaseBuilder(
                context.applicationContext,
                ObdForgeDatabase::class.java,
                ObdForgeDatabase.DB_NAME,
            )
                .addMigrations(ObdForgeDatabase.MIGRATION_1_2)
                .build()
            return ObdForgeCompositionRoot(
                transportRegistry = TransportRegistry.default(),
                protocolRegistry = ProtocolRegistry.default(),
                database = database,
            )
        }
    }
}
