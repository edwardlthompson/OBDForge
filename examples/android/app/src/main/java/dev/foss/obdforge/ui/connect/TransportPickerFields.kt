package dev.foss.obdforge.ui.connect

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.data.transport.BluetoothDeviceOption
import dev.foss.obdforge.data.transport.UsbDeviceOption
import dev.foss.obdforge.domain.transport.BluetoothLinkKind

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothDevicePickerField(
    devices: List<BluetoothDeviceOption>,
    selectedAddress: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (BluetoothDeviceOption) -> Unit,
) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
        TextField(
            value = devices.firstOrNull { it.address == selectedAddress }
                ?.let { it.name ?: it.address }
                ?: stringResource(R.string.transport_bluetooth_none),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.transport_bluetooth_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            devices.forEach { device ->
                DropdownMenuItem(
                    text = { Text(device.name ?: device.address) },
                    onClick = {
                        onSelect(device)
                        onExpandedChange(false)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothLinkKindPickerField(
    selected: BluetoothLinkKind,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (BluetoothLinkKind) -> Unit,
) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
        TextField(
            value = bluetoothLinkKindLabel(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.transport_link_kind_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            BluetoothLinkKind.entries.forEach { kind ->
                DropdownMenuItem(
                    text = { Text(bluetoothLinkKindLabel(kind)) },
                    onClick = {
                        onSelect(kind)
                        onExpandedChange(false)
                    },
                )
            }
        }
    }
}

@Composable
fun BluetoothPairingActions(
    selectedAddress: String,
    isBonded: Boolean,
    isPairing: Boolean,
    onPair: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    if (selectedAddress.isBlank()) return
    Text(
        text = stringResource(R.string.bluetooth_pair_hint),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.fillMaxWidth(),
    )
    if (!isBonded) {
        Button(
            onClick = onPair,
            enabled = !isPairing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(
                    if (isPairing) R.string.bluetooth_pairing else R.string.bluetooth_pair_adapter,
                ),
            )
        }
        Button(
            onClick = { openBluetoothSettings(context) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.bluetooth_pair_open_settings))
        }
    }
}

fun openBluetoothSettings(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}

@Composable
fun bluetoothLinkKindLabel(kind: BluetoothLinkKind): String =
    when (kind) {
        BluetoothLinkKind.Classic -> stringResource(R.string.transport_link_kind_classic)
        BluetoothLinkKind.Ble -> stringResource(R.string.transport_link_kind_ble)
        BluetoothLinkKind.Auto -> stringResource(R.string.transport_link_kind_auto)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsbDevicePickerField(
    devices: List<UsbDeviceOption>,
    selectedDeviceName: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (UsbDeviceOption) -> Unit,
    onRequestPermission: () -> Unit,
) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
        TextField(
            value = devices.firstOrNull { it.deviceName == selectedDeviceName }
                ?.deviceName
                ?: stringResource(R.string.transport_usb_none),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.transport_usb_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            devices.forEach { device ->
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(
                                R.string.transport_usb_device_entry,
                                device.deviceName,
                                device.vendorId,
                                device.productId,
                            ),
                        )
                    },
                    onClick = {
                        onSelect(device)
                        onExpandedChange(false)
                    },
                )
            }
        }
    }
    Button(
        onClick = onRequestPermission,
        modifier = Modifier.fillMaxWidth(),
        enabled = selectedDeviceName.isNotBlank(),
    ) {
        Text(stringResource(R.string.transport_usb_permission))
    }
}

@Composable
fun transportTypeLabel(type: dev.foss.obdforge.domain.transport.TransportType): String =
    when (type) {
        dev.foss.obdforge.domain.transport.TransportType.Bluetooth ->
            stringResource(R.string.transport_type_bluetooth)
        dev.foss.obdforge.domain.transport.TransportType.UsbSerial ->
            stringResource(R.string.transport_type_usb)
        dev.foss.obdforge.domain.transport.TransportType.WiFi ->
            stringResource(R.string.transport_type_wifi)
        dev.foss.obdforge.domain.transport.TransportType.Ethernet ->
            stringResource(R.string.transport_type_ethernet)
        dev.foss.obdforge.domain.transport.TransportType.Simulated ->
            stringResource(R.string.transport_type_simulated)
    }
