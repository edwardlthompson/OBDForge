package dev.foss.obdforge.ui.livedata

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.obdforge.domain.livedata.PersonaMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LiveDataHost(
    coordinator: LiveDataCoordinator,
    scope: CoroutineScope,
    persona: PersonaMode,
    onBack: () -> Unit,
) {
    val snapshot by coordinator.snapshot.collectAsStateWithLifecycle(
        initialValue = dev.foss.obdforge.domain.livedata.LiveDataSnapshot(emptyMap(), 0),
    )
    val paused by coordinator.paused.collectAsStateWithLifecycle(initialValue = false)

    DisposableEffect(coordinator, persona) {
        scope.launch {
            coordinator.start(scope, persona)
        }
        onDispose {
            coordinator.stop(scope)
        }
    }

    LiveDataDashboardScreen(
        snapshot = snapshot,
        persona = persona,
        paused = paused,
        onPauseToggle = {
            if (paused) coordinator.resume() else coordinator.pause()
        },
        onBack = onBack,
    )
}
