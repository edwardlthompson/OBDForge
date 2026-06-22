package dev.foss.obdforge.data.transport

import android.bluetooth.BluetoothManager
import android.content.Context
import dev.foss.obdforge.data.transport.io.BleGattTransportLink
import dev.foss.obdforge.data.transport.io.BluetoothSppTransportLink
import dev.foss.obdforge.data.transport.io.FallbackTransportLink
import dev.foss.obdforge.data.transport.io.TransportLink
import dev.foss.obdforge.domain.transport.BluetoothLinkKind
import dev.foss.obdforge.domain.transport.TransportEndpoint

internal object BluetoothTransportLinks {
    fun create(context: Context, endpoint: TransportEndpoint.Bluetooth): TransportLink? {
        val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter ?: return null
        val classic = BluetoothSppTransportLink(adapter, endpoint.deviceAddress)
        val ble = BleGattTransportLink(context, adapter, endpoint.deviceAddress)
        return when (endpoint.linkKind) {
            BluetoothLinkKind.Classic -> classic
            BluetoothLinkKind.Ble -> ble
            BluetoothLinkKind.Auto -> FallbackTransportLink(listOf(ble, classic))
        }
    }
}
