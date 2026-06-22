package dev.foss.goldenpath.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R
import dev.foss.goldenpath.about.DonationsConfig
import dev.foss.goldenpath.ui.about.AboutScreen
import dev.foss.goldenpath.ui.components.ThemeToggle
import dev.foss.goldenpath.ui.settings.SettingsScreen
import dev.foss.goldenpath.ui.theme.SpacingLg
import dev.foss.goldenpath.ui.theme.SpacingMd
import dev.foss.goldenpath.ui.theme.ThemeMode
import dev.foss.obdforge.data.transport.BluetoothDeviceOption
import dev.foss.obdforge.data.transport.UsbDeviceOption
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.ui.connect.TransportPickerCard
import dev.foss.obdforge.ui.livedata.LiveDataEntryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldenPathScreen(
    themeMode: ThemeMode,
    isOnline: Boolean,
    demoModeEnabled: Boolean,
    connectionStatus: String,
    vinDisplay: String,
    vinSourceLabel: String,
    showAbout: Boolean,
    showSettings: Boolean,
    updateCheckEnabled: Boolean,
    appVersion: String,
    installedFormat: String,
    updateStatus: String,
    donations: DonationsConfig,
    canApplyUpdate: Boolean,
    onThemeToggle: () -> Unit,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onDemoModeChange: (Boolean) -> Unit,
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
    onRequestUsbPermission: () -> Unit,
    onAboutOpen: () -> Unit,
    onAboutClose: () -> Unit,
    onSettingsOpen: () -> Unit,
    onSettingsClose: () -> Unit,
    onUpdateCheckChange: (Boolean) -> Unit,
    onApplyUpdate: () -> Unit,
    liveDataEnabled: Boolean = false,
    onOpenLiveData: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_title)) },
                actions = {
                    IconButton(onClick = onSettingsOpen) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings_open),
                        )
                    }
                    IconButton(onClick = onAboutOpen) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.about_open),
                        )
                    }
                    ThemeToggle(themeMode = themeMode, onToggle = onThemeToggle)
                },
            )
        },
    ) { innerPadding ->
        when {
            showSettings -> SettingsScreen(
                themeMode = themeMode,
                updateCheckEnabled = updateCheckEnabled,
                onThemeModeSelect = onThemeModeSelect,
                onUpdateCheckChange = onUpdateCheckChange,
                onBack = onSettingsClose,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            showAbout -> AboutScreen(
                version = appVersion,
                installedFormat = installedFormat,
                updateStatus = updateStatus,
                donations = donations,
                canApplyUpdate = canApplyUpdate,
                onApplyUpdate = onApplyUpdate,
                onBack = onAboutClose,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(SpacingMd),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpacingMd),
            ) {
                Text(
                    text = stringResource(R.string.app_greeting),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = connectionStatus,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = vinDisplay,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = vinSourceLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(
                        if (demoModeEnabled) R.string.demo_mode_on else R.string.demo_mode_off,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
                if (!demoModeEnabled) {
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
                        onRequestUsbPermission = onRequestUsbPermission,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Switch(
                    checked = demoModeEnabled,
                    onCheckedChange = onDemoModeChange,
                    colors = SwitchDefaults.colors(),
                )
                Text(
                    text = stringResource(R.string.demo_mode_label),
                    style = MaterialTheme.typography.labelMedium,
                )
                LiveDataEntryButton(
                    enabled = liveDataEnabled,
                    onOpen = onOpenLiveData,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(
                        if (isOnline) R.string.app_status_online else R.string.app_status_offline,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val currentUpdateLabel = stringResource(R.string.about_update_current)
                if (updateStatus != currentUpdateLabel) {
                    Text(
                        text = updateStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
    }
}
