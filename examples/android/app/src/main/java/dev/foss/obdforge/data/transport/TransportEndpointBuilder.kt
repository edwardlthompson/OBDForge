package dev.foss.obdforge.data.transport

import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType

fun buildTransportEndpoint(
    type: TransportType,
    tcpHost: String,
    tcpPort: String,
    bluetoothAddress: String,
    bluetoothName: String?,
    usbDeviceName: String,
): TransportEndpoint? {
    return when (type) {
        TransportType.Simulated -> TransportEndpoint.Simulated
        TransportType.WiFi, TransportType.Ethernet -> {
            val port = tcpPort.toIntOrNull() ?: return null
            if (tcpHost.isBlank()) return null
            TransportEndpoint.Tcp(host = tcpHost.trim(), port = port)
        }
        TransportType.Bluetooth -> {
            if (bluetoothAddress.isBlank()) return null
            TransportEndpoint.Bluetooth(deviceAddress = bluetoothAddress, displayName = bluetoothName)
        }
        TransportType.UsbSerial -> {
            if (usbDeviceName.isBlank()) return null
            TransportEndpoint.UsbSerial(deviceName = usbDeviceName)
        }
    }
}
