package dev.foss.obdforge.data.transport

import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.domain.transport.BluetoothLinkKind
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedTransportConnectTest {
    private val lastBluetooth = TransportEndpoint.Bluetooth(
        deviceAddress = "AA:BB:CC:DD:EE:FF",
        displayName = "OBDLink MX+",
        linkKind = BluetoothLinkKind.Auto,
    )

    @Test
    fun isReady_bluetoothUsesLastKnownWhenAddressBlank() {
        val saved = TransportSelection(
            TransportType.Bluetooth,
            TransportEndpoint.Bluetooth(deviceAddress = "", displayName = null, linkKind = BluetoothLinkKind.Auto),
        )
        assertFalse(SavedTransportConnect.isReady(saved))
        assertTrue(SavedTransportConnect.isReady(saved, lastBluetooth))
    }

    @Test
    fun resolveSelection_prefersLastBluetoothWhenSavedAddressBlank() {
        val saved = TransportSelection(
            TransportType.Bluetooth,
            TransportEndpoint.Bluetooth(deviceAddress = "", displayName = null, linkKind = BluetoothLinkKind.Auto),
        )
        val resolved = SavedTransportConnect.resolveSelection(saved, lastBluetooth)
        assertNotNull(resolved)
        assertTrue(resolved!!.endpoint is TransportEndpoint.Bluetooth)
        assertTrue((resolved.endpoint as TransportEndpoint.Bluetooth).deviceAddress.isNotBlank())
    }

    @Test
    fun resolveSelection_usbRequiresDeviceName() {
        val saved = TransportSelection(
            TransportType.UsbSerial,
            TransportEndpoint.UsbSerial(deviceName = ""),
        )
        assertNull(SavedTransportConnect.resolveSelection(saved, lastBluetooth))
    }
}
