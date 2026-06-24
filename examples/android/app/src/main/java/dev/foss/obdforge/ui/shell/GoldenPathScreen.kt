package dev.foss.obdforge.ui.shell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.about.DonationsConfig
import dev.foss.obdforge.ui.about.AboutScreen
import dev.foss.obdforge.ui.components.ThemeToggle
import dev.foss.obdforge.ui.theme.ObdScaffold
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.obdBottomGutter
import dev.foss.obdforge.ui.theme.ThemeMode
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.transport.BluetoothDeviceOption
import dev.foss.obdforge.data.transport.UsbDeviceOption
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VehicleProfile
import dev.foss.obdforge.ui.connect.BluetoothConnectButton
import dev.foss.obdforge.ui.connect.TransportPickerCard
import dev.foss.obdforge.ui.vin.VinBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldenPathScreen(
    themeMode: ThemeMode,
    isOnline: Boolean,
    demoModeEnabled: Boolean,
    personaMode: PersonaMode,
    connectionStatus: String,
    vinDisplay: String,
    vinSourceLabel: String,
    savedVehicleProfile: VehicleProfile? = null,
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
    onPersonaChange: (PersonaMode) -> Unit,
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
    bluetoothConnectEnabled: Boolean = false,
    bluetoothConnectConnecting: Boolean = false,
    bluetoothLastAdapterLabel: String? = null,
    bluetoothConnectStatusMessage: String = "",
    onBluetoothConnect: () -> Unit = {},
    onAboutOpen: () -> Unit,
    onAboutClose: () -> Unit,
    onSettingsOpen: () -> Unit,
    onSettingsClose: () -> Unit,
    onReviewPermissions: () -> Unit,
    onUpdateCheckChange: (Boolean) -> Unit,
    onApplyUpdate: () -> Unit,
    liveDataEnabled: Boolean = false,
    onOpenLiveData: () -> Unit = {},
    onOpenSessionHistory: () -> Unit = {},
    onOpenVinResolve: () -> Unit = {},
    onOpenShop: () -> Unit = {},
    onOpenDtcExplain: () -> Unit = {},
    compositionRoot: ObdForgeCompositionRoot? = null,
    settingsScope: kotlinx.coroutines.CoroutineScope? = null,
) {
    ObdScaffold(
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
            showSettings && compositionRoot != null && settingsScope != null -> {
                val settingsContext = LocalContext.current
                GoldenPathSettingsHost(
                    context = settingsContext,
                    scope = settingsScope,
                    root = compositionRoot,
                    themeMode = themeMode,
                    updateCheckEnabled = updateCheckEnabled,
                    demoModeEnabled = demoModeEnabled,
                    personaMode = personaMode,
                    onThemeModeSelect = onThemeModeSelect,
                    onUpdateCheckChange = onUpdateCheckChange,
                    onDemoModeChange = onDemoModeChange,
                    onPersonaChange = onPersonaChange,
                    onBack = onSettingsClose,
                    onReviewPermissions = onReviewPermissions,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
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
                savedVehicleProfile?.let { profile ->
                    VinBadge(source = profile.source)
                }
                if (!demoModeEnabled) {
                    BluetoothConnectButton(
                        enabled = bluetoothConnectEnabled,
                        isConnecting = bluetoothConnectConnecting,
                        lastAdapterLabel = bluetoothLastAdapterLabel,
                        onConnect = onBluetoothConnect,
                    )
                    if (bluetoothConnectStatusMessage.isNotBlank()) {
                        Text(
                            text = bluetoothConnectStatusMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
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
