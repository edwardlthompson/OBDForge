package dev.foss.obdforge.ui.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.data.transport.BluetoothDeviceOption
import dev.foss.obdforge.data.transport.UsbDeviceOption
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.ui.connect.AdapterConnectCard
import dev.foss.obdforge.ui.connect.TransportPickerCard
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.obdBottomGutter

@Composable
fun GoldenPathHomeContent(
    demoModeEnabled: Boolean,
    personaMode: PersonaMode,
    vinDisplay: String,
    vinSourceLabel: String,
    adapterConnectUi: AdapterConnectUi,
    transportPickerType: TransportType,
    transportTcpHost: String,
    transportTcpPort: String,
    transportBluetoothAddress: String,
    transportUsbDeviceName: String,
    bluetoothDevices: List<BluetoothDeviceOption>,
    usbDevices: List<UsbDeviceOption>,
    transportStatusMessage: String,
    onTransportTypeChange: (TransportType) -> Unit,
    onTransportTcpHostChange: (String) -> Unit,
    onTransportTcpPortChange: (String) -> Unit,
    onBluetoothSelect: (BluetoothDeviceOption) -> Unit,
    onUsbSelect: (UsbDeviceOption) -> Unit,
    onSaveTransportSelection: () -> Unit,
    onSaveAndConnect: () -> Unit,
    onRequestUsbPermission: () -> Unit,
    liveDataEnabled: Boolean,
    onOpenLiveData: () -> Unit,
    onOpenSessionHistory: () -> Unit,
    onOpenVinResolve: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenDtcExplain: () -> Unit,
    updateStatus: String,
    currentUpdateLabel: String,
    modifier: Modifier = Modifier,
) {
    var showTransportPicker by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .obdBottomGutter(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.app_greeting),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        if (!demoModeEnabled) {
            AdapterConnectCard(
                isConnected = adapterConnectUi.isConnected,
                isConnecting = adapterConnectUi.isConnecting,
                adapterSummary = adapterConnectUi.adapterSummary,
                transportLabel = adapterConnectUi.transportLabel,
                vinDisplay = vinDisplay,
                vinSourceLabel = vinSourceLabel,
                statusMessage = adapterConnectUi.statusMessage,
                canConnect = adapterConnectUi.canConnect || adapterConnectUi.showSetupHint,
                showSetupHint = adapterConnectUi.showSetupHint,
                onConnect = {
                    if (adapterConnectUi.showSetupHint) {
                        showTransportPicker = true
                    } else {
                        adapterConnectUi.onConnect()
                    }
                },
                onChangeAdapter = { showTransportPicker = !showTransportPicker },
            )
        } else {
            Text(
                text = stringResource(R.string.demo_mode_on),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = vinDisplay,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        GoldenPathHomeNav(
            persona = personaMode,
            liveDataEnabled = liveDataEnabled,
            onOpenLiveData = onOpenLiveData,
            onOpenSessionHistory = onOpenSessionHistory,
            onOpenVinResolve = onOpenVinResolve,
            onOpenShop = onOpenShop,
            onOpenDtcExplain = onOpenDtcExplain,
        )
        if (!demoModeEnabled) {
            AnimatedVisibility(visible = showTransportPicker || adapterConnectUi.showSetupHint) {
                TransportPickerCard(
                    selectedType = transportPickerType,
                    tcpHost = transportTcpHost,
                    tcpPort = transportTcpPort,
                    selectedBluetoothAddress = transportBluetoothAddress,
                    selectedUsbDeviceName = transportUsbDeviceName,
                    bluetoothDevices = bluetoothDevices,
                    usbDevices = usbDevices,
                    statusMessage = transportStatusMessage,
                    onTypeChange = onTransportTypeChange,
                    onTcpHostChange = onTransportTcpHostChange,
                    onTcpPortChange = onTransportTcpPortChange,
                    onBluetoothSelect = onBluetoothSelect,
                    onUsbSelect = onUsbSelect,
                    onSaveSelection = onSaveTransportSelection,
                    onSaveAndConnect = onSaveAndConnect,
                    onRequestUsbPermission = onRequestUsbPermission,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        if (updateStatus != currentUpdateLabel) {
            Text(
                text = updateStatus,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}
