package dev.foss.obdforge.data

import android.content.Context
import androidx.room.Room
import dev.foss.obdforge.data.bidirectional.GatedBidirectionalService
import dev.foss.obdforge.data.bidirectional.ObdBidirectionalExecutor
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.preferences.DemoPreferences
import dev.foss.obdforge.data.preferences.DiagnosticLogPreferences
import dev.foss.obdforge.data.preferences.ExpertUnlockPreferences
import dev.foss.obdforge.data.preferences.PersonaPreferences
import dev.foss.obdforge.data.preferences.TransportPreferences
import dev.foss.obdforge.data.diagnostics.CrashLogHandler
import dev.foss.obdforge.data.diagnostics.DiagnosticEventRecorder
import dev.foss.obdforge.data.persistence.AuditLogRepository
import dev.foss.obdforge.data.persistence.DiagnosticEventRepository
import dev.foss.obdforge.data.persistence.SafetyGateUseCase
import dev.foss.obdforge.data.persistence.SessionRecorder
import dev.foss.obdforge.data.persistence.SessionRepository
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.data.diagnostics.VehicleHealthScanUseCase
import dev.foss.obdforge.data.transport.AdapterConnectUseCase
import dev.foss.obdforge.data.transport.TransportDiscovery
import dev.foss.obdforge.data.ai.DtcManufacturerOverlayLoader
import dev.foss.obdforge.data.ai.DtcCatalogAssetLoader
import dev.foss.obdforge.data.ai.ExplainDtcUseCase
import dev.foss.obdforge.data.ai.LocalAiEngineFactory
import dev.foss.obdforge.data.diagnostics.PidRangeAssetLoader
import dev.foss.obdforge.data.shop.ShopRepository
import dev.foss.obdforge.data.vin.ResolveVinUseCase
import dev.foss.obdforge.data.vin.VinProfileRepository

import kotlinx.coroutines.CoroutineScope

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
    val diagnosticEventRepository: DiagnosticEventRepository,
    val diagnosticLogPreferences: DiagnosticLogPreferences,
    val diagnosticEventRecorder: DiagnosticEventRecorder,
    val safetyGateUseCase: SafetyGateUseCase,
    val gatedBidirectionalService: GatedBidirectionalService,
    val vinProfileRepository: VinProfileRepository,
    val resolveVinUseCase: ResolveVinUseCase,
    val shopRepository: ShopRepository,
    val explainDtcUseCase: ExplainDtcUseCase,
    val sessionRecorder: SessionRecorder,
    val adapterConnectUseCase: AdapterConnectUseCase,
    val vehicleHealthScanUseCase: VehicleHealthScanUseCase,
) {
    companion object {
        fun create(context: Context, scope: CoroutineScope? = null): ObdForgeCompositionRoot {
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
                    ObdForgeDatabase.MIGRATION_5_6,
                    ObdForgeDatabase.MIGRATION_6_7,
                )
                .build()
            val sessionRepository = SessionRepository(database)
            val auditLogRepository = AuditLogRepository(database)
            val diagnosticEventRepository = DiagnosticEventRepository(database)
            val diagnosticLogPreferences = DiagnosticLogPreferences(appContext)
            val diagnosticEventRecorder = DiagnosticEventRecorder(
                repository = diagnosticEventRepository,
                preferences = diagnosticLogPreferences,
                scope = scope ?: CoroutineScope(kotlinx.coroutines.Dispatchers.IO),
            )
            val vinProfileRepository = VinProfileRepository(database)
            val resolveVinUseCase = ResolveVinUseCase(vinProfileRepository)
            val shopRepository = ShopRepository(database)
            val explainDtcUseCase = LocalAiEngineFactory.createExplainDtcUseCase(appContext)
            val transportRegistry = TransportRegistry.default(appContext, diagnosticEventRecorder)
            val protocolRegistry = ProtocolRegistry.default()
            val transportPreferences = TransportPreferences(appContext)
            val safetyGateUseCase = SafetyGateUseCase(auditLogRepository)
            DtcCatalogAssetLoader.loadIntoCatalog(appContext)
            DtcManufacturerOverlayLoader.loadIntoCatalog(appContext)
            PidRangeAssetLoader.loadIntoEvaluator(appContext)
            return ObdForgeCompositionRoot(
                transportRegistry = transportRegistry,
                protocolRegistry = protocolRegistry,
                database = database,
                transportPreferences = transportPreferences,
                transportDiscovery = TransportDiscovery(appContext),
                personaPreferences = PersonaPreferences(appContext),
                demoPreferences = DemoPreferences(appContext),
                expertUnlockPreferences = ExpertUnlockPreferences(appContext),
                sessionRepository = sessionRepository,
                auditLogRepository = auditLogRepository,
                diagnosticEventRepository = diagnosticEventRepository,
                diagnosticLogPreferences = diagnosticLogPreferences,
                diagnosticEventRecorder = diagnosticEventRecorder,
                safetyGateUseCase = safetyGateUseCase,
                gatedBidirectionalService = GatedBidirectionalService(
                    executor = ObdBidirectionalExecutor(transportRegistry, protocolRegistry),
                    safetyGateUseCase = safetyGateUseCase,
                ),
                vinProfileRepository = vinProfileRepository,
                resolveVinUseCase = resolveVinUseCase,
                shopRepository = shopRepository,
                explainDtcUseCase = explainDtcUseCase,
                sessionRecorder = SessionRecorder(
                    transportRegistry = transportRegistry,
                    protocolRegistry = protocolRegistry,
                    sessionRepository = sessionRepository,
                ),
                adapterConnectUseCase = AdapterConnectUseCase(
                    transportRegistry = transportRegistry,
                    protocolRegistry = protocolRegistry,
                    transportPreferences = transportPreferences,
                    eventRecorder = diagnosticEventRecorder,
                ),
                vehicleHealthScanUseCase = VehicleHealthScanUseCase(
                    transportRegistry = transportRegistry,
                    protocolRegistry = protocolRegistry,
                ),
            )
        }
    }
}
