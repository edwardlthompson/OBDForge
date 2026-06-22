package dev.foss.obdforge.ui.connect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R
import dev.foss.goldenpath.ui.theme.SpacingMd
import dev.foss.obdforge.data.transport.BluetoothDeviceOption
import dev.foss.obdforge.data.transport.UsbDeviceOption
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportPickerCard(
    selectedType: TransportType,
    tcpHost: String,
    tcpPort: String,
    selectedBluetoothAddress: String,
    selectedUsbDeviceName: String,
    bluetoothDevices: List<BluetoothDeviceOption>,
    usbDevices: List<UsbDeviceOption>,
    statusMessage: String,
    onTypeChange: (TransportType) -> Unit,
    onTcpHostChange: (String) -> Unit,
    onTcpPortChange: (String) -> Unit,
    onBluetoothSelect: (BluetoothDeviceOption) -> Unit,
    onUsbSelect: (UsbDeviceOption) -> Unit,
    onSaveSelection: () -> Unit,
    onRequestUsbPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var typeExpanded by remember { mutableStateOf(false) }
    var btExpanded by remember { mutableStateOf(false) }
    var usbExpanded by remember { mutableStateOf(false) }
    val transportTypes = remember {
        listOf(
            TransportType.WiFi,
            TransportType.Ethernet,
            TransportType.Bluetooth,
            TransportType.UsbSerial,
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Text(
                text = stringResource(R.string.transport_picker_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it },
            ) {
                TextField(
                    value = transportTypeLabel(selectedType),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.transport_type_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false },
                ) {
                    transportTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(transportTypeLabel(type)) },
                            onClick = {
                                onTypeChange(type)
                                typeExpanded = false
                            },
                        )
                    }
                }
            }
            when (selectedType) {
                TransportType.WiFi, TransportType.Ethernet -> {
                    OutlinedTextField(
                        value = tcpHost,
                        onValueChange = onTcpHostChange,
                        label = { Text(stringResource(R.string.transport_tcp_host_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = tcpPort,
                        onValueChange = onTcpPortChange,
                        label = { Text(stringResource(R.string.transport_tcp_port_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
                TransportType.Bluetooth -> {
                    ExposedDropdownMenuBox(
                        expanded = btExpanded,
                        onExpandedChange = { btExpanded = it },
                    ) {
                        TextField(
                            value = bluetoothDevices.firstOrNull { it.address == selectedBluetoothAddress }
                                ?.let { it.name ?: it.address }
                                ?: stringResource(R.string.transport_bluetooth_none),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.transport_bluetooth_label)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = btExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                        )
                        ExposedDropdownMenu(
                            expanded = btExpanded,
                            onDismissRequest = { btExpanded = false },
                        ) {
                            bluetoothDevices.forEach { device ->
                                DropdownMenuItem(
                                    text = { Text(device.name ?: device.address) },
                                    onClick = {
                                        onBluetoothSelect(device)
                                        btExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }
                TransportType.UsbSerial -> {
                    ExposedDropdownMenuBox(
                        expanded = usbExpanded,
                        onExpandedChange = { usbExpanded = it },
                    ) {
                        TextField(
                            value = usbDevices.firstOrNull { it.deviceName == selectedUsbDeviceName }
                                ?.deviceName
                                ?: stringResource(R.string.transport_usb_none),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.transport_usb_label)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = usbExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                        )
                        ExposedDropdownMenu(
                            expanded = usbExpanded,
                            onDismissRequest = { usbExpanded = false },
                        ) {
                            usbDevices.forEach { device ->
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
                                        onUsbSelect(device)
                                        usbExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    Button(
                        onClick = onRequestUsbPermission,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedUsbDeviceName.isNotBlank(),
                    ) {
                        Text(stringResource(R.string.transport_usb_permission))
                    }
                }
                TransportType.Simulated -> Unit
            }
            if (statusMessage.isNotBlank()) {
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = onSaveSelection,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.transport_save_selection))
            }
        }
    }
}

@Composable
private fun transportTypeLabel(type: TransportType): String =
    when (type) {
        TransportType.Bluetooth -> stringResource(R.string.transport_type_bluetooth)
        TransportType.UsbSerial -> stringResource(R.string.transport_type_usb)
        TransportType.WiFi -> stringResource(R.string.transport_type_wifi)
        TransportType.Ethernet -> stringResource(R.string.transport_type_ethernet)
        TransportType.Simulated -> stringResource(R.string.transport_type_simulated)
    }

fun buildEndpoint(
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
