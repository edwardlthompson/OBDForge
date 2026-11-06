package dev.foss.obdforge.ui.shell

import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.domain.transport.BluetoothLinkKind
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType

internal object GoldenPathDefaults {
    val bluetoothSelection: TransportSelection = TransportSelection(
        type = TransportType.Bluetooth,
        endpoint = TransportEndpoint.Bluetooth(
            deviceAddress = "",
            displayName = null,
            linkKind = BluetoothLinkKind.Auto,
        ),
    )

    val demoSelection: TransportSelection =
        TransportSelection(TransportType.Simulated, TransportEndpoint.Simulated)
}
