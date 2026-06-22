package dev.foss.obdforge.data

import android.content.Context
import androidx.room.Room
import dev.foss.obdforge.data.bidirectional.GatedBidirectionalService
import dev.foss.obdforge.data.bidirectional.ObdBidirectionalExecutor
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
import dev.foss.obdforge.data.vin.ResolveVinUseCase
import dev.foss.obdforge.data.vin.VinProfileRepository

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
    val gatedBidirectionalService: GatedBidirectionalService,
    val vinProfileRepository: VinProfileRepository,
    val resolveVinUseCase: ResolveVinUseCase,
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
                    ObdForgeDatabase.MIGRATION_4_5,
                )
                .build()
            val sessionRepository = SessionRepository(database)
            val auditLogRepository = AuditLogRepository(database)
            val vinProfileRepository = VinProfileRepository(database)
            val resolveVinUseCase = ResolveVinUseCase(vinProfileRepository)
            val transportRegistry = TransportRegistry.default(appContext)
            val protocolRegistry = ProtocolRegistry.default()
            val safetyGateUseCase = SafetyGateUseCase(auditLogRepository)
            return ObdForgeCompositionRoot(
                transportRegistry = transportRegistry,
                protocolRegistry = protocolRegistry,
                database = database,
                transportPreferences = TransportPreferences(appContext),
                transportDiscovery = TransportDiscovery(appContext),
                personaPreferences = PersonaPreferences(appContext),
                demoPreferences = DemoPreferences(appContext),
                expertUnlockPreferences = ExpertUnlockPreferences(appContext),
                sessionRepository = sessionRepository,
                auditLogRepository = auditLogRepository,
                safetyGateUseCase = safetyGateUseCase,
                gatedBidirectionalService = GatedBidirectionalService(
                    executor = ObdBidirectionalExecutor(transportRegistry, protocolRegistry),
                    safetyGateUseCase = safetyGateUseCase,
                ),
                vinProfileRepository = vinProfileRepository,
                resolveVinUseCase = resolveVinUseCase,
                sessionRecorder = SessionRecorder(
                    transportRegistry = transportRegistry,
                    protocolRegistry = protocolRegistry,
                    sessionRepository = sessionRepository,
                ),
            )
        }
    }
}
