package dev.foss.goldenpath.ui

import androidx.compose.runtime.Composable
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.vehicle.VehicleProfile
import dev.foss.obdforge.ui.livedata.LiveDataCoordinator
import dev.foss.obdforge.ui.livedata.LiveDataHost
import dev.foss.obdforge.ui.session.SessionHistoryCoordinator
import dev.foss.obdforge.ui.session.SessionHistoryHost
import dev.foss.obdforge.ui.shop.ShopHost
import dev.foss.obdforge.ui.ai.DtcExplainHost
import dev.foss.obdforge.ui.vin.VinResolveHost
import kotlinx.coroutines.CoroutineScope

enum class GoldenPathRoute {
    Home,
    LiveData,
    SessionHistory,
    VinResolve,
    Shop,
    DtcExplain,
}

@Composable
fun GoldenPathRouteHost(
    route: GoldenPathRoute,
    root: ObdForgeCompositionRoot,
    scope: CoroutineScope,
    personaMode: PersonaMode,
    demoModeEnabled: Boolean,
    activeTransportSelection: TransportSelection,
    savedVehicleProfile: VehicleProfile?,
    liveDataCoordinator: LiveDataCoordinator,
    sessionHistoryCoordinator: SessionHistoryCoordinator,
    onRouteChange: (GoldenPathRoute) -> Unit,
    homeContent: @Composable () -> Unit,
) {
    when (route) {
        GoldenPathRoute.LiveData -> LiveDataHost(
            coordinator = liveDataCoordinator,
            scope = scope,
            persona = personaMode,
            onBack = { onRouteChange(GoldenPathRoute.Home) },
        )
        GoldenPathRoute.SessionHistory -> SessionHistoryHost(
            coordinator = sessionHistoryCoordinator,
            scope = scope,
            onBack = { onRouteChange(GoldenPathRoute.Home) },
        )
        GoldenPathRoute.VinResolve -> VinResolveHost(
            root = root,
            scope = scope,
            demoModeEnabled = demoModeEnabled,
            transportSelection = activeTransportSelection,
            onBack = { onRouteChange(GoldenPathRoute.Home) },
        )
        GoldenPathRoute.Shop -> ShopHost(
            root = root,
            scope = scope,
            savedVehicleProfile = savedVehicleProfile,
            onBack = { onRouteChange(GoldenPathRoute.Home) },
        )
        GoldenPathRoute.DtcExplain -> DtcExplainHost(
            root = root,
            scope = scope,
            persona = personaMode,
            onBack = { onRouteChange(GoldenPathRoute.Home) },
        )
        GoldenPathRoute.Home -> homeContent()
    }
}
