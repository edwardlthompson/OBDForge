package dev.foss.obdforge.ui.shell

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.about.DonationsConfig
import dev.foss.obdforge.ui.about.AboutScreen
import dev.foss.obdforge.ui.components.ThemeToggle
import dev.foss.obdforge.ui.theme.ObdScaffold
import dev.foss.obdforge.ui.theme.ThemeMode
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.transport.BluetoothDeviceOption
import dev.foss.obdforge.data.transport.UsbDeviceOption
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.transport.BluetoothLinkKind
import dev.foss.obdforge.domain.transport.TransportType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldenPathScreen(
    themeMode: ThemeMode,
    demoModeEnabled: Boolean,
    personaMode: PersonaMode,
    vinDisplay: String,
    vinSourceLabel: String,
    adapterConnectUi: AdapterConnectUi,
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
    bluetoothLinkKind: BluetoothLinkKind,
    bluetoothBonded: Boolean,
    bluetoothPairing: Boolean,
    transportStatusMessage: String,
    onTransportTypeChange: (TransportType) -> Unit,
    onTransportTcpHostChange: (String) -> Unit,
    onTransportTcpPortChange: (String) -> Unit,
    onBluetoothSelect: (BluetoothDeviceOption) -> Unit,
    onBluetoothLinkKindChange: (BluetoothLinkKind) -> Unit,
    onPairBluetooth: () -> Unit,
    onUsbSelect: (UsbDeviceOption) -> Unit,
    onSaveTransportSelection: () -> Unit,
    onSaveAndConnect: () -> Unit,
    onRequestUsbPermission: () -> Unit,
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
    onOpenEcuCoding: () -> Unit = {},
    compositionRoot: ObdForgeCompositionRoot? = null,
    settingsScope: kotlinx.coroutines.CoroutineScope? = null,
) {
    val currentUpdateLabel = stringResource(R.string.about_update_current)
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
            else -> GoldenPathHomeContent(
                demoModeEnabled = demoModeEnabled,
                personaMode = personaMode,
                vinDisplay = vinDisplay,
                vinSourceLabel = vinSourceLabel,
                adapterConnectUi = adapterConnectUi,
                transportPickerType = transportPickerType,
                transportTcpHost = transportTcpHost,
                transportTcpPort = transportTcpPort,
                transportBluetoothAddress = transportBluetoothAddress,
                transportUsbDeviceName = transportUsbDeviceName,
                bluetoothDevices = bluetoothDevices,
                usbDevices = usbDevices,
                bluetoothLinkKind = bluetoothLinkKind,
                bluetoothBonded = bluetoothBonded,
                bluetoothPairing = bluetoothPairing,
                transportStatusMessage = transportStatusMessage,
                onTransportTypeChange = onTransportTypeChange,
                onTransportTcpHostChange = onTransportTcpHostChange,
                onTransportTcpPortChange = onTransportTcpPortChange,
                onBluetoothSelect = onBluetoothSelect,
                onBluetoothLinkKindChange = onBluetoothLinkKindChange,
                onPairBluetooth = onPairBluetooth,
                onUsbSelect = onUsbSelect,
                onSaveTransportSelection = onSaveTransportSelection,
                onSaveAndConnect = onSaveAndConnect,
                onRequestUsbPermission = onRequestUsbPermission,
                liveDataEnabled = liveDataEnabled,
                onOpenLiveData = onOpenLiveData,
                onOpenSessionHistory = onOpenSessionHistory,
                onOpenVinResolve = onOpenVinResolve,
                onOpenShop = onOpenShop,
                onOpenDtcExplain = onOpenDtcExplain,
                onOpenEcuCoding = onOpenEcuCoding,
                updateStatus = updateStatus,
                currentUpdateLabel = currentUpdateLabel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}
