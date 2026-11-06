package dev.foss.obdforge.data

import android.content.Context
import dev.foss.obdforge.data.bidirectional.GatedBidirectionalService
import dev.foss.obdforge.data.flash.GatedFlashService
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.preferences.DebugPreferences
import dev.foss.obdforge.data.preferences.DemoPreferences
import dev.foss.obdforge.data.preferences.DiagnosticLogPreferences
import dev.foss.obdforge.data.preferences.ExpertUnlockPreferences
import dev.foss.obdforge.data.preferences.PersonaPreferences
import dev.foss.obdforge.data.preferences.TransportPreferences
import dev.foss.obdforge.data.transport.SimulatedTransportSelector
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
import dev.foss.obdforge.data.ai.ExplainDtcUseCase
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
    val debugPreferences: DebugPreferences,
    val simulatedTransportSelector: SimulatedTransportSelector,
    val expertUnlockPreferences: ExpertUnlockPreferences,
    val sessionRepository: SessionRepository,
    val auditLogRepository: AuditLogRepository,
    val diagnosticEventRepository: DiagnosticEventRepository,
    val diagnosticLogPreferences: DiagnosticLogPreferences,
    val diagnosticEventRecorder: DiagnosticEventRecorder,
    val safetyGateUseCase: SafetyGateUseCase,
    val gatedBidirectionalService: GatedBidirectionalService,
    val gatedFlashService: GatedFlashService,
    val vinProfileRepository: VinProfileRepository,
    val resolveVinUseCase: ResolveVinUseCase,
    val shopRepository: ShopRepository,
    val explainDtcUseCase: ExplainDtcUseCase,
    val sessionRecorder: SessionRecorder,
    val adapterConnectUseCase: AdapterConnectUseCase,
    val vehicleHealthScanUseCase: VehicleHealthScanUseCase,
) {
    companion object {
        fun create(context: Context, scope: CoroutineScope? = null): ObdForgeCompositionRoot =
            ObdForgeCompositionRootFactory.create(context, scope)
    }
}
