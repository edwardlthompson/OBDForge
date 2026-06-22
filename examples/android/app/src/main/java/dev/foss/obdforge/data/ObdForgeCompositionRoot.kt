package dev.foss.obdforge.data

import android.content.Context
import androidx.room.Room
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.preferences.DemoPreferences
import dev.foss.obdforge.data.preferences.ExpertUnlockPreferences
import dev.foss.obdforge.data.preferences.PersonaPreferences
import dev.foss.obdforge.data.preferences.TransportPreferences
import dev.foss.obdforge.data.persistence.AuditLogRepository
import dev.foss.obdforge.data.persistence.SafetyGateUseCase
import dev.foss.obdforge.data.persistence.SessionRecorder
import dev.foss.obdforge.data.persistence.SessionRepository
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.data.transport.TransportDiscovery

data class ObdForgeCompositionRoot(
    val transportRegistry: TransportRegistry,
    val protocolRegistry: ProtocolRegistry,
    val database: ObdForgeDatabase,
    val transportPreferences: TransportPreferences,
    val transportDiscovery: TransportDiscovery,
    val personaPreferences: PersonaPreferences,
    val demoPreferences: DemoPreferences,
    val expertUnlockPreferences: ExpertUnlockPreferences,
    val sessionRepository: SessionRepository,
    val auditLogRepository: AuditLogRepository,
    val safetyGateUseCase: SafetyGateUseCase,
    val sessionRecorder: SessionRecorder,
) {
    companion object {
        fun create(context: Context): ObdForgeCompositionRoot {
            val appContext = context.applicationContext
            val database = Room.databaseBuilder(
                appContext,
                ObdForgeDatabase::class.java,
                ObdForgeDatabase.DB_NAME,
            )
                .addMigrations(
                    ObdForgeDatabase.MIGRATION_1_2,
                    ObdForgeDatabase.MIGRATION_2_3,
                    ObdForgeDatabase.MIGRATION_3_4,
                )
                .build()
            val sessionRepository = SessionRepository(database)
            val auditLogRepository = AuditLogRepository(database)
            return ObdForgeCompositionRoot(
                transportRegistry = TransportRegistry.default(appContext),
                protocolRegistry = ProtocolRegistry.default(),
                database = database,
                transportPreferences = TransportPreferences(appContext),
                transportDiscovery = TransportDiscovery(appContext),
                personaPreferences = PersonaPreferences(appContext),
                demoPreferences = DemoPreferences(appContext),
                expertUnlockPreferences = ExpertUnlockPreferences(appContext),
                sessionRepository = sessionRepository,
                auditLogRepository = auditLogRepository,
                safetyGateUseCase = SafetyGateUseCase(auditLogRepository),
                sessionRecorder = SessionRecorder(
                    transportRegistry = TransportRegistry.default(appContext),
                    protocolRegistry = ProtocolRegistry.default(),
                    sessionRepository = sessionRepository,
                ),
            )
        }
    }
}
