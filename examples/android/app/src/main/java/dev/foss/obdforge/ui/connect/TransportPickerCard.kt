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
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.data.transport.BluetoothDeviceOption
import dev.foss.obdforge.data.transport.UsbDeviceOption
import dev.foss.obdforge.domain.transport.BluetoothLinkKind
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
    bluetoothLinkKind: BluetoothLinkKind,
    bluetoothBonded: Boolean,
    bluetoothPairing: Boolean,
    statusMessage: String,
    onTypeChange: (TransportType) -> Unit,
    onTcpHostChange: (String) -> Unit,
    onTcpPortChange: (String) -> Unit,
    onBluetoothSelect: (BluetoothDeviceOption) -> Unit,
    onBluetoothLinkKindChange: (BluetoothLinkKind) -> Unit,
    onPairBluetooth: () -> Unit,
    onUsbSelect: (UsbDeviceOption) -> Unit,
    onSaveSelection: () -> Unit,
    onSaveAndConnect: () -> Unit,
    onRequestUsbPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var typeExpanded by remember { mutableStateOf(false) }
    var btExpanded by remember { mutableStateOf(false) }
    var linkExpanded by remember { mutableStateOf(false) }
    var usbExpanded by remember { mutableStateOf(false) }
    val transportTypes = remember {
        listOf(
            TransportType.WiFi,
            TransportType.Ethernet,
            TransportType.Bluetooth,
            TransportType.UsbSerial,
        )
    }
    val canConnectBluetooth = selectedType != TransportType.Bluetooth ||
        (selectedBluetoothAddress.isNotBlank() && bluetoothBonded)

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
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
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
                    BluetoothDevicePickerField(
                        devices = bluetoothDevices,
                        selectedAddress = selectedBluetoothAddress,
                        expanded = btExpanded,
                        onExpandedChange = { btExpanded = it },
                        onSelect = onBluetoothSelect,
                    )
                    BluetoothLinkKindPickerField(
                        selected = bluetoothLinkKind,
                        expanded = linkExpanded,
                        onExpandedChange = { linkExpanded = it },
                        onSelect = onBluetoothLinkKindChange,
                    )
                    BluetoothPairingActions(
                        selectedAddress = selectedBluetoothAddress,
                        isBonded = bluetoothBonded,
                        isPairing = bluetoothPairing,
                        onPair = onPairBluetooth,
                    )
                }
                TransportType.UsbSerial -> UsbDevicePickerField(
                    devices = usbDevices,
                    selectedDeviceName = selectedUsbDeviceName,
                    expanded = usbExpanded,
                    onExpandedChange = { usbExpanded = it },
                    onSelect = onUsbSelect,
                    onRequestPermission = onRequestUsbPermission,
                )
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
                onClick = onSaveAndConnect,
                modifier = Modifier.fillMaxWidth(),
                enabled = canConnectBluetooth,
            ) {
                Text(stringResource(R.string.transport_save_and_connect))
            }
            Button(onClick = onSaveSelection, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.transport_save_selection))
            }
        }
    }
}
