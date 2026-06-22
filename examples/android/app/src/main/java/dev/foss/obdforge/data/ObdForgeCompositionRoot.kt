package dev.foss.obdforge.data

import android.content.Context
import androidx.room.Room
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.preferences.TransportPreferences
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.data.transport.TransportDiscovery

data class ObdForgeCompositionRoot(
    val transportRegistry: TransportRegistry,
    val protocolRegistry: ProtocolRegistry,
    val database: ObdForgeDatabase,
    val transportPreferences: TransportPreferences,
    val transportDiscovery: TransportDiscovery,
) {
    companion object {
        fun create(context: Context): ObdForgeCompositionRoot {
            val appContext = context.applicationContext
            val database = Room.databaseBuilder(
                appContext,
                ObdForgeDatabase::class.java,
                ObdForgeDatabase.DB_NAME,
            )
                .addMigrations(ObdForgeDatabase.MIGRATION_1_2)
                .build()
            return ObdForgeCompositionRoot(
                transportRegistry = TransportRegistry.default(appContext),
                protocolRegistry = ProtocolRegistry.default(),
                database = database,
                transportPreferences = TransportPreferences(appContext),
                transportDiscovery = TransportDiscovery(appContext),
            )
        }
    }
}
