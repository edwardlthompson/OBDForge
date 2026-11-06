package dev.foss.obdforge.ui.shell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.foss.obdforge.BuildConfig
import dev.foss.obdforge.data.ObdForgeCompositionRoot

/** Keeps simulated transport selector in sync with debug DataStore (debug builds only). */
@Composable
fun GoldenPathMockTransportEffect(
    root: ObdForgeCompositionRoot,
    useMockTransport: Boolean,
    mockSessionId: String,
) {
    LaunchedEffect(useMockTransport, mockSessionId, root) {
        root.simulatedTransportSelector.useMockTransport = BuildConfig.DEBUG && useMockTransport
        root.simulatedTransportSelector.mockSessionId = mockSessionId
    }
}
