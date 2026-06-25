package dev.foss.obdforge.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.foss.obdforge.domain.transport.BluetoothLinkKind
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.transportDataStore: DataStore<Preferences> by preferencesDataStore(name = "transport_preferences")

data class TransportSelection(
    val type: TransportType,
    val endpoint: TransportEndpoint,
)

class TransportPreferences(private val context: Context) {
    val selection: Flow<TransportSelection> = context.transportDataStore.data.map { prefs ->
        val typeName = prefs[TYPE_KEY] ?: TransportType.Bluetooth.name
        val type = runCatching { TransportType.valueOf(typeName) }.getOrDefault(TransportType.Bluetooth)
        TransportSelection(type = type, endpoint = endpointFromPrefs(type, prefs))
    }

    suspend fun setSelection(type: TransportType, endpoint: TransportEndpoint) {
        context.transportDataStore.edit { prefs ->
            prefs[TYPE_KEY] = type.name
            when (endpoint) {
                TransportEndpoint.Simulated -> {
                    prefs.remove(TCP_HOST_KEY)
                    prefs.remove(TCP_PORT_KEY)
                    prefs.remove(BT_ADDRESS_KEY)
                    prefs.remove(BT_NAME_KEY)
                    prefs.remove(BT_LINK_KIND_KEY)
                    prefs.remove(USB_DEVICE_KEY)
                }
                is TransportEndpoint.Tcp -> {
                    prefs[TCP_HOST_KEY] = endpoint.host
                    prefs[TCP_PORT_KEY] = endpoint.port
                }
                is TransportEndpoint.Bluetooth -> {
                    prefs[BT_ADDRESS_KEY] = endpoint.deviceAddress
                    endpoint.displayName?.let { prefs[BT_NAME_KEY] = it } ?: prefs.remove(BT_NAME_KEY)
                    prefs[BT_LINK_KIND_KEY] = endpoint.linkKind.name
                    prefs[LAST_BT_ADDRESS_KEY] = endpoint.deviceAddress
                    endpoint.displayName?.let { prefs[LAST_BT_NAME_KEY] = it } ?: prefs.remove(LAST_BT_NAME_KEY)
                    prefs[LAST_BT_LINK_KIND_KEY] = endpoint.linkKind.name
                }
                is TransportEndpoint.UsbSerial -> {
                    prefs[USB_DEVICE_KEY] = endpoint.deviceName
                    prefs[USB_BAUD_KEY] = endpoint.baudRate
                }
            }
        }
    }

    val lastBluetooth: Flow<TransportEndpoint.Bluetooth?> =
        context.transportDataStore.data.map { readLastBluetooth(it) }

    suspend fun lastBluetoothEndpoint(): TransportEndpoint.Bluetooth? =
        lastBluetooth.first()

    private fun readLastBluetooth(prefs: Preferences): TransportEndpoint.Bluetooth? {
        val address = prefs[LAST_BT_ADDRESS_KEY] ?: prefs[BT_ADDRESS_KEY]
        if (address.isNullOrBlank()) return null
        val kindName = prefs[LAST_BT_LINK_KIND_KEY] ?: prefs[BT_LINK_KIND_KEY]
        val linkKind = kindName?.let { runCatching { BluetoothLinkKind.valueOf(it) }.getOrNull() }
            ?: BluetoothLinkKind.Auto
        return TransportEndpoint.Bluetooth(
            deviceAddress = address,
            displayName = prefs[LAST_BT_NAME_KEY] ?: prefs[BT_NAME_KEY],
            linkKind = linkKind,
        )
    }

    private fun endpointFromPrefs(type: TransportType, prefs: Preferences): TransportEndpoint =
        when (type) {
            TransportType.Simulated -> TransportEndpoint.Simulated
            TransportType.WiFi, TransportType.Ethernet -> TransportEndpoint.Tcp(
                host = prefs[TCP_HOST_KEY] ?: TransportEndpoint.Tcp.DEFAULT_OBD_HOST,
                port = prefs[TCP_PORT_KEY] ?: TransportEndpoint.Tcp.DEFAULT_OBD_PORT,
            )
            TransportType.Bluetooth -> TransportEndpoint.Bluetooth(
                deviceAddress = prefs[BT_ADDRESS_KEY].orEmpty(),
                displayName = prefs[BT_NAME_KEY],
                linkKind = prefs[BT_LINK_KIND_KEY]?.let {
                    runCatching { BluetoothLinkKind.valueOf(it) }.getOrDefault(BluetoothLinkKind.Auto)
                } ?: BluetoothLinkKind.Auto,
            )
            TransportType.UsbSerial -> TransportEndpoint.UsbSerial(
                deviceName = prefs[USB_DEVICE_KEY].orEmpty(),
                baudRate = prefs[USB_BAUD_KEY] ?: TransportEndpoint.UsbSerial.DEFAULT_BAUD,
            )
        }

    companion object {
        private val TYPE_KEY = stringPreferencesKey("transport_type")
        private val TCP_HOST_KEY = stringPreferencesKey("tcp_host")
        private val TCP_PORT_KEY = intPreferencesKey("tcp_port")
        private val BT_ADDRESS_KEY = stringPreferencesKey("bt_address")
        private val BT_NAME_KEY = stringPreferencesKey("bt_name")
        private val BT_LINK_KIND_KEY = stringPreferencesKey("bt_link_kind")
        private val LAST_BT_ADDRESS_KEY = stringPreferencesKey("last_bt_address")
        private val LAST_BT_NAME_KEY = stringPreferencesKey("last_bt_name")
        private val LAST_BT_LINK_KIND_KEY = stringPreferencesKey("last_bt_link_kind")
        private val USB_DEVICE_KEY = stringPreferencesKey("usb_device")
        private val USB_BAUD_KEY = intPreferencesKey("usb_baud")
    }
}
