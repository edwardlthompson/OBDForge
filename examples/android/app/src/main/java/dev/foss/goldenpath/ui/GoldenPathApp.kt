package dev.foss.goldenpath.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.goldenpath.BuildConfig
import dev.foss.goldenpath.R
import dev.foss.goldenpath.about.AppUpdatePreferences
import dev.foss.goldenpath.about.DonationsLoader
import dev.foss.goldenpath.network.NetworkStatusMonitor
import dev.foss.goldenpath.settings.SettingsLogic
import dev.foss.goldenpath.ui.theme.GoldenPathTheme
import dev.foss.goldenpath.ui.theme.ThemeMode
import dev.foss.goldenpath.ui.theme.ThemePreferences
import dev.foss.goldenpath.ui.theme.next
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.DemoPreferences
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.ui.demo.DemoModeShell
import dev.foss.obdforge.ui.connect.ConnectDemoCoordinator
import dev.foss.obdforge.ui.livedata.LiveDataCoordinator
import dev.foss.obdforge.ui.livedata.LiveDataHost
import dev.foss.obdforge.ui.session.SessionHistoryCoordinator
import dev.foss.obdforge.ui.session.SessionHistoryHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun GoldenPathApp(
    context: Context,
    scope: CoroutineScope,
    themePreferences: ThemePreferences,
    appUpdatePreferences: AppUpdatePreferences,
    networkStatusMonitor: NetworkStatusMonitor,
    compositionRoot: ObdForgeCompositionRoot? = null,
) {
    val themeMode by themePreferences.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.System)
    val isOnline by networkStatusMonitor.isOnline.collectAsStateWithLifecycle(initialValue = true)
    val installedFormat by appUpdatePreferences.installedFormat.collectAsStateWithLifecycle(initialValue = "apk")
    val checkInterval by appUpdatePreferences.checkInterval.collectAsStateWithLifecycle(initialValue = "off")
    val lastChecked by appUpdatePreferences.lastChecked.collectAsStateWithLifecycle(initialValue = null)
    val pendingRestart by appUpdatePreferences.pendingRestart.collectAsStateWithLifecycle(initialValue = false)
    var showAbout by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val donations = remember { DonationsLoader.load(context) }
    val appVersion = BuildConfig.VERSION_NAME
    val activity = context as? ComponentActivity

    var showLiveData by remember { mutableStateOf(false) }
    var showSessionHistory by remember { mutableStateOf(false) }
    var connectionStatus by remember {
        mutableStateOf(context.getString(R.string.connection_status_disconnected))
    }
    var vinDisplay by remember { mutableStateOf(context.getString(R.string.vin_unknown)) }
    var vinSourceLabel by remember { mutableStateOf("") }
    val root = compositionRoot ?: remember { ObdForgeCompositionRoot.create(context) }
    val demoModeEnabled by root.demoPreferences.enabled.collectAsStateWithLifecycle(
        initialValue = DemoPreferences.DEFAULT_ENABLED,
    )
    val savedTransport by root.transportPreferences.selection.collectAsStateWithLifecycle(
        initialValue = TransportSelection(
            type = TransportType.WiFi,
            endpoint = TransportEndpoint.Tcp(
                host = TransportEndpoint.Tcp.DEFAULT_OBD_HOST,
                port = TransportEndpoint.Tcp.DEFAULT_OBD_PORT,
            ),
        ),
    )
    val transportUi = rememberGoldenPathTransportUi(
        context = context,
        scope = scope,
        root = root,
        activity = activity,
        onConnectionStatusChange = { connectionStatus = it },
    )
    val demoSelection = remember {
        TransportSelection(TransportType.Simulated, TransportEndpoint.Simulated)
    }
    val connectDemo = remember(root, demoModeEnabled, savedTransport) {
        ConnectDemoCoordinator(
            transportRegistry = root.transportRegistry,
            protocolRegistry = root.protocolRegistry,
            selection = if (demoModeEnabled) demoSelection else savedTransport,
        )
    }

    val personaMode by root.personaPreferences.persona.collectAsStateWithLifecycle(
        initialValue = PersonaMode.Diy,
    )
    val liveDataCoordinator = remember(root, demoModeEnabled, savedTransport) {
        LiveDataCoordinator(
            transportRegistry = root.transportRegistry,
            protocolRegistry = root.protocolRegistry,
            selection = if (demoModeEnabled) demoSelection else savedTransport,
        )
    }

    val sessionHistoryCoordinator = remember(root) {
        SessionHistoryCoordinator(root.sessionRepository)
    }

    GoldenPathDemoConnectionEffect(
        demoModeEnabled = demoModeEnabled,
        connectDemo = connectDemo,
        demoSelection = demoSelection,
        savedTransport = savedTransport,
        root = root,
        context = context,
        onConnectionStatusChange = { connectionStatus = it },
        onVinDisplayChange = { vinDisplay = it },
        onVinSourceLabelChange = { vinSourceLabel = it },
    )

    val updateUi = rememberGoldenPathUpdateUi(
        context = context,
        scope = scope,
        activity = activity,
        appVersion = appVersion,
        appUpdatePreferences = appUpdatePreferences,
        isOnline = isOnline,
        installedFormat = installedFormat,
        checkInterval = checkInterval,
        lastChecked = lastChecked,
        pendingRestart = pendingRestart,
    )

    GoldenPathTheme(themeMode = themeMode) {
        DemoModeShell(demoModeEnabled = demoModeEnabled) {
        when {
            showLiveData -> LiveDataHost(
                coordinator = liveDataCoordinator,
                scope = scope,
                persona = personaMode,
                onPersonaChange = { mode -> scope.launch { root.personaPreferences.setPersona(mode) } },
                onBack = { showLiveData = false },
            )
            showSessionHistory -> SessionHistoryHost(
                coordinator = sessionHistoryCoordinator,
                scope = scope,
                onBack = { showSessionHistory = false },
            )
            else -> GoldenPathScreen(
            themeMode = themeMode,
            isOnline = isOnline,
            demoModeEnabled = demoModeEnabled,
            connectionStatus = connectionStatus,
            vinDisplay = vinDisplay,
            vinSourceLabel = vinSourceLabel,
            showAbout = showAbout,
            showSettings = showSettings,
            updateCheckEnabled = SettingsLogic.isUpdateCheckEnabled(checkInterval),
            appVersion = appVersion,
            installedFormat = installedFormat ?: "apk",
            updateStatus = updateUi.updateStatus,
            donations = donations,
            canApplyUpdate = updateUi.canApplyUpdate,
            onThemeToggle = { scope.launch { themePreferences.setThemeMode(themeMode.next()) } },
            onThemeModeSelect = { mode -> scope.launch { themePreferences.setThemeMode(mode) } },
            onDemoModeChange = { enabled ->
                scope.launch { root.demoPreferences.setEnabled(enabled) }
            },
            transportPickerType = transportUi.pickerType,
            transportTcpHost = transportUi.tcpHost,
            transportTcpPort = transportUi.tcpPort,
            transportBluetoothAddress = transportUi.bluetoothAddress,
            transportUsbDeviceName = transportUi.usbDeviceName,
            bluetoothDevices = transportUi.bluetoothDevices,
            usbDevices = transportUi.usbDevices,
            transportStatusMessage = transportUi.statusMessage,
            onTransportTypeChange = transportUi.onTypeChange,
            onTransportTcpHostChange = transportUi.onTcpHostChange,
            onTransportTcpPortChange = transportUi.onTcpPortChange,
            onBluetoothSelect = transportUi.onBluetoothSelect,
            onUsbSelect = transportUi.onUsbSelect,
            onSaveTransportSelection = transportUi.onSaveSelection,
            onRequestUsbPermission = transportUi.onRequestUsbPermission,
            onAboutOpen = { showAbout = !showAbout; if (showAbout) showSettings = false },
            onAboutClose = { showAbout = false },
            onSettingsOpen = { showSettings = !showSettings; if (showSettings) showAbout = false },
            onSettingsClose = { showSettings = false },
            onUpdateCheckChange = { enabled ->
                scope.launch {
                    appUpdatePreferences.setCheckInterval(
                        SettingsLogic.intervalForToggle(enabled, checkInterval),
                    )
                }
            },
            onApplyUpdate = updateUi.onApplyUpdate,
            liveDataEnabled = demoModeEnabled && connectionStatus.contains("Connected"),
            onOpenLiveData = { showLiveData = true },
            onOpenSessionHistory = { showSessionHistory = true },
            compositionRoot = root,
            settingsScope = scope,
        )
        }
        }
    }
}
