package dev.foss.obdforge.ui.shell

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.obdforge.R
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.transport.BluetoothBonding
import dev.foss.obdforge.data.transport.BluetoothDeviceOption
import dev.foss.obdforge.data.transport.UsbDeviceOption
import dev.foss.obdforge.data.transport.buildTransportEndpoint
import dev.foss.obdforge.data.transport.displayLabel
import dev.foss.obdforge.domain.transport.BluetoothAdapterHints
import dev.foss.obdforge.domain.transport.BluetoothLinkKind
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.ui.connect.BluetoothPermissionGate
import dev.foss.obdforge.ui.connect.UsbPermissionRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class GoldenPathTransportUi(
    val pickerType: TransportType,
    val tcpHost: String,
    val tcpPort: String,
    val bluetoothAddress: String,
    val usbDeviceName: String,
    val bluetoothDevices: List<BluetoothDeviceOption>,
    val usbDevices: List<UsbDeviceOption>,
    val bluetoothLinkKind: BluetoothLinkKind,
    val bluetoothBonded: Boolean,
    val bluetoothPairing: Boolean,
    val statusMessage: String,
    val onTypeChange: (TransportType) -> Unit,
    val onTcpHostChange: (String) -> Unit,
    val onTcpPortChange: (String) -> Unit,
    val onBluetoothSelect: (BluetoothDeviceOption) -> Unit,
    val onBluetoothLinkKindChange: (BluetoothLinkKind) -> Unit,
    val onPairBluetooth: () -> Unit,
    val onUsbSelect: (UsbDeviceOption) -> Unit,
    val onSaveSelection: () -> Unit,
    val onSaveAndConnect: () -> Unit,
    val onRequestUsbPermission: () -> Unit,
)

@Composable
fun rememberGoldenPathTransportUi(
    context: Context,
    scope: CoroutineScope,
    root: ObdForgeCompositionRoot,
    activity: ComponentActivity?,
    onConnectionStatusChange: (String) -> Unit,
    onConnectAfterSave: () -> Unit,
): GoldenPathTransportUi {
    val savedTransport by root.transportPreferences.selection.collectAsStateWithLifecycle(
        initialValue = TransportSelection(
            type = TransportType.Bluetooth,
            endpoint = TransportEndpoint.Bluetooth(
                deviceAddress = "",
                displayName = null,
                linkKind = BluetoothLinkKind.Auto,
            ),
        ),
    )
    var pickerType by remember(savedTransport.type) { mutableStateOf(savedTransport.type) }
    var tcpHost by remember(savedTransport) {
        mutableStateOf((savedTransport.endpoint as? TransportEndpoint.Tcp)?.host.orEmpty())
    }
    var tcpPort by remember(savedTransport) {
        mutableStateOf(
            ((savedTransport.endpoint as? TransportEndpoint.Tcp)?.port
                ?: TransportEndpoint.Tcp.DEFAULT_OBD_PORT).toString(),
        )
    }
    var bluetoothAddress by remember(savedTransport) {
        mutableStateOf((savedTransport.endpoint as? TransportEndpoint.Bluetooth)?.deviceAddress.orEmpty())
    }
    var bluetoothName by remember(savedTransport) {
        mutableStateOf((savedTransport.endpoint as? TransportEndpoint.Bluetooth)?.displayName)
    }
    var bluetoothLinkKind by remember(savedTransport) {
        mutableStateOf(
            (savedTransport.endpoint as? TransportEndpoint.Bluetooth)?.linkKind
                ?: BluetoothLinkKind.Auto,
        )
    }
    var usbDeviceName by remember(savedTransport) {
        mutableStateOf((savedTransport.endpoint as? TransportEndpoint.UsbSerial)?.deviceName.orEmpty())
    }
    var transportStatus by remember { mutableStateOf("") }
    var bluetoothDevices by remember { mutableStateOf(emptyList<BluetoothDeviceOption>()) }
    var usbDevices by remember { mutableStateOf(emptyList<UsbDeviceOption>()) }
    var bluetoothBonded by remember { mutableStateOf(false) }
    var bluetoothPairing by remember { mutableStateOf(false) }
    val usbPermissionRequester = remember(activity) {
        activity?.let { UsbPermissionRequester(it) }
    }

    LaunchedEffect(pickerType) {
        if (pickerType == TransportType.Bluetooth) {
            val hasBlePermission = BluetoothPermissionGate.hasAllPermissions(context)
            bluetoothDevices = if (hasBlePermission) {
                root.transportDiscovery.discoverBluetoothDevices()
            } else {
                root.transportDiscovery.pairedBluetoothDevices()
            }
        } else {
            bluetoothDevices = emptyList()
        }
        usbDevices = if (pickerType == TransportType.UsbSerial) {
            root.transportDiscovery.attachedUsbSerialDevices()
        } else {
            emptyList()
        }
    }

    LaunchedEffect(bluetoothAddress) {
        bluetoothBonded = if (bluetoothAddress.isBlank()) {
            false
        } else {
            BluetoothBonding.isBonded(context, bluetoothAddress)
        }
    }

    fun saveEndpoint(connectAfter: Boolean) {
        val endpoint = buildTransportEndpoint(
            type = pickerType,
            tcpHost = tcpHost,
            tcpPort = tcpPort,
            bluetoothAddress = bluetoothAddress,
            bluetoothName = bluetoothName,
            usbDeviceName = usbDeviceName,
            bluetoothLinkKind = bluetoothLinkKind,
        )
        if (endpoint == null) {
            transportStatus = context.getString(R.string.transport_status_invalid)
            return
        }
        if (connectAfter && pickerType == TransportType.Bluetooth && !bluetoothBonded) {
            transportStatus = context.getString(R.string.bluetooth_connect_error_not_bonded)
            return
        }
        scope.launch {
            root.transportPreferences.setSelection(pickerType, endpoint)
            transportStatus = context.getString(
                R.string.transport_status_saved,
                transportTypeName(context, pickerType),
                endpoint.displayLabel(),
            )
            onConnectionStatusChange(context.getString(R.string.connection_status_adapter_ready))
            if (connectAfter) onConnectAfterSave()
        }
    }

    return GoldenPathTransportUi(
        pickerType = pickerType,
        tcpHost = tcpHost,
        tcpPort = tcpPort,
        bluetoothAddress = bluetoothAddress,
        usbDeviceName = usbDeviceName,
        bluetoothDevices = bluetoothDevices,
        usbDevices = usbDevices,
        bluetoothLinkKind = bluetoothLinkKind,
        bluetoothBonded = bluetoothBonded,
        bluetoothPairing = bluetoothPairing,
        statusMessage = transportStatus,
        onTypeChange = { pickerType = it },
        onTcpHostChange = { tcpHost = it },
        onTcpPortChange = { tcpPort = it },
        onBluetoothSelect = { device ->
            bluetoothAddress = device.address
            bluetoothName = device.name
            bluetoothLinkKind = BluetoothAdapterHints.defaultLinkKind(device.name)
            bluetoothBonded = BluetoothBonding.isBonded(context, device.address)
        },
        onBluetoothLinkKindChange = { bluetoothLinkKind = it },
        onPairBluetooth = {
            if (bluetoothAddress.isBlank() || bluetoothPairing) return@GoldenPathTransportUi
            bluetoothPairing = true
            transportStatus = context.getString(R.string.bluetooth_pairing)
            scope.launch {
                val ok = BluetoothBonding.ensureBonded(context, bluetoothAddress)
                bluetoothPairing = false
                bluetoothBonded = ok || BluetoothBonding.isBonded(context, bluetoothAddress)
                transportStatus = if (bluetoothBonded) {
                    context.getString(R.string.connection_status_adapter_ready)
                } else {
                    context.getString(R.string.bluetooth_pair_failed)
                }
            }
        },
        onUsbSelect = { device -> usbDeviceName = device.deviceName },
        onSaveSelection = { saveEndpoint(connectAfter = false) },
        onSaveAndConnect = { saveEndpoint(connectAfter = true) },
        onRequestUsbPermission = {
            val requester = usbPermissionRequester
            val device = requester?.deviceForName(usbDeviceName)
            if (requester != null && device != null) {
                requester.requestPermission(device) { granted ->
                    transportStatus = context.getString(
                        if (granted) R.string.transport_status_usb_granted else R.string.transport_status_usb_denied,
                    )
                }
            }
        },
    )
}

private fun transportTypeName(context: Context, type: TransportType): String =
    context.getString(
        when (type) {
            TransportType.Bluetooth -> R.string.transport_type_bluetooth
            TransportType.UsbSerial -> R.string.transport_type_usb
            TransportType.WiFi -> R.string.transport_type_wifi
            TransportType.Ethernet -> R.string.transport_type_ethernet
            TransportType.Simulated -> R.string.transport_type_simulated
        },
    )
