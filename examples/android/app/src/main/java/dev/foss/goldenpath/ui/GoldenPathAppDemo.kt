package dev.foss.goldenpath.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.foss.goldenpath.R
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.domain.vehicle.VinSourceType
import dev.foss.obdforge.ui.connect.ConnectDemoCoordinator

@Composable
fun GoldenPathDemoConnectionEffect(
    demoModeEnabled: Boolean,
    connectDemo: ConnectDemoCoordinator,
    demoSelection: TransportSelection,
    savedTransport: TransportSelection,
    root: ObdForgeCompositionRoot,
    context: Context,
    onConnectionStatusChange: (String) -> Unit,
    onVinDisplayChange: (String) -> Unit,
    onVinSourceLabelChange: (String) -> Unit,
) {
    LaunchedEffect(demoModeEnabled) {
        if (!demoModeEnabled) {
            connectDemo.disconnect()
            onConnectionStatusChange(context.getString(R.string.connection_status_disconnected))
            onVinDisplayChange(context.getString(R.string.vin_unknown))
            onVinSourceLabelChange("")
            return@LaunchedEffect
        }
        onConnectionStatusChange(context.getString(R.string.vin_reading))
        onVinDisplayChange(context.getString(R.string.vin_unknown))
        val vinResult = connectDemo.connectAndReadVin()
        onConnectionStatusChange(context.getString(R.string.connection_status_connected))
        onVinDisplayChange(context.getString(R.string.vin_label, vinResult.vin))
        onVinSourceLabelChange(
            when (vinResult.source) {
                VinSourceType.EcuObd2 -> context.getString(R.string.vin_source_ecu)
                VinSourceType.Demo -> context.getString(R.string.vin_source_demo)
                else -> ""
            },
        )
        root.sessionRecorder.recordFromConnection(
            selection = if (demoModeEnabled) demoSelection else savedTransport,
            vinResult = vinResult,
        )
    }
}
