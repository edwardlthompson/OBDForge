package dev.foss.goldenpath.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.persona.AppDestination
import dev.foss.obdforge.domain.persona.PersonaNavigation
import dev.foss.obdforge.ui.livedata.LiveDataEntryButton
import dev.foss.obdforge.ui.session.SessionHistoryEntryButton
import dev.foss.obdforge.ui.shop.ShopEntryButton
import dev.foss.obdforge.ui.ai.DtcExplainEntryButton
import dev.foss.obdforge.ui.vin.VinResolveEntryButton

@Composable
fun ColumnScope.GoldenPathHomeNav(
    persona: PersonaMode,
    liveDataEnabled: Boolean,
    onOpenLiveData: () -> Unit,
    onOpenSessionHistory: () -> Unit,
    onOpenVinResolve: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenDtcExplain: () -> Unit,
) {
    if (PersonaNavigation.isVisible(persona, AppDestination.Shop)) {
        ShopEntryButton(onOpen = onOpenShop, modifier = Modifier.fillMaxWidth())
    }
    if (PersonaNavigation.isVisible(persona, AppDestination.DtcExplain)) {
        DtcExplainEntryButton(onOpen = onOpenDtcExplain, modifier = Modifier.fillMaxWidth())
    }
    if (PersonaNavigation.isVisible(persona, AppDestination.VinResolve)) {
        VinResolveEntryButton(onOpen = onOpenVinResolve, modifier = Modifier.fillMaxWidth())
    }
    if (PersonaNavigation.isVisible(persona, AppDestination.LiveData)) {
        LiveDataEntryButton(
            enabled = liveDataEnabled,
            onOpen = onOpenLiveData,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    if (PersonaNavigation.isVisible(persona, AppDestination.SessionHistory)) {
        SessionHistoryEntryButton(
            onOpen = onOpenSessionHistory,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
