package dev.foss.obdforge.data.transport

import android.content.Context
import dev.foss.obdforge.data.demo.SimulatedObdTransport
import dev.foss.obdforge.data.transport.io.TcpTransportLink
import dev.foss.obdforge.data.transport.io.UsbSerialTransportLink
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType

object TransportFactory {
    fun create(
        context: Context,
        type: TransportType,
        endpoint: TransportEndpoint,
    ): ObdTransport? {
        return when (type) {
            TransportType.Simulated -> SimulatedObdTransport()
            TransportType.WiFi, TransportType.Ethernet -> {
                val tcp = endpoint as? TransportEndpoint.Tcp ?: return null
                StreamObdTransport(
                    type = type,
                    endpoint = endpoint,
                    link = TcpTransportLink(tcp.host, tcp.port),
                )
            }
            TransportType.Bluetooth -> {
                val bt = endpoint as? TransportEndpoint.Bluetooth ?: return null
                val link = BluetoothTransportLinks.create(context, bt) ?: return null
                StreamObdTransport(
                    type = type,
                    endpoint = endpoint,
                    link = link,
                )
            }
            TransportType.UsbSerial -> {
                val usb = endpoint as? TransportEndpoint.UsbSerial ?: return null
                StreamObdTransport(
                    type = type,
                    endpoint = endpoint,
                    link = UsbSerialTransportLink(context, usb.deviceName, usb.baudRate),
                )
            }
        }
    }
}
