package dev.foss.obdforge.ui.shell

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.obdforge.BuildConfig
import dev.foss.obdforge.R
import dev.foss.obdforge.about.AppUpdatePreferences
import dev.foss.obdforge.about.DonationsLoader
import dev.foss.obdforge.network.NetworkStatusMonitor
import dev.foss.obdforge.settings.SettingsLogic
import dev.foss.obdforge.ui.theme.GoldenPathTheme
import dev.foss.obdforge.ui.theme.ThemeMode
import dev.foss.obdforge.ui.theme.ThemePreferences
import dev.foss.obdforge.ui.theme.next
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.DemoPreferences
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.preferences.WelcomePreferences
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.ui.demo.DemoModeShell
import dev.foss.obdforge.ui.connect.ConnectDemoCoordinator
import dev.foss.obdforge.ui.livedata.LiveDataCoordinator
import dev.foss.obdforge.ui.session.SessionHistoryCoordinator
import dev.foss.obdforge.ui.welcome.WelcomeHost
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
    var route by remember { mutableStateOf(GoldenPathRoute.Home) }
    val donations = remember { DonationsLoader.load(context) }
    val appVersion = BuildConfig.VERSION_NAME
    val activity = context as? ComponentActivity
    val welcomePreferences = remember { WelcomePreferences(context) }
    val welcomeCompleted by welcomePreferences.completed.collectAsStateWithLifecycle(initialValue = false)
    var showWelcomeReview by remember { mutableStateOf(false) }

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
    val bluetoothConnectUi = rememberBluetoothConnectUi(
        context = context,
        scope = scope,
        root = root,
        demoModeEnabled = demoModeEnabled,
        onConnectionStatusChange = { connectionStatus = it },
        onVinDisplayChange = { vinDisplay = it },
        onVinSourceLabelChange = { vinSourceLabel = it },
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
            eventRecorder = root.diagnosticEventRecorder,
        )
    }

    val sessionHistoryCoordinator = remember(root) {
        SessionHistoryCoordinator(root.sessionRepository)
    }

    val savedVehicleProfile by root.vinProfileRepository.observeLatest()
        .collectAsStateWithLifecycle(initialValue = null)
    val activeTransportSelection = if (demoModeEnabled) demoSelection else savedTransport

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
        if (!welcomeCompleted || showWelcomeReview) {
            WelcomeHost(
                activity = activity,
                onContinue = {
                    scope.launch {
                        welcomePreferences.setCompleted(true)
                        showWelcomeReview = false
                    }
                },
            )
            return@GoldenPathTheme
        }
        DemoModeShell(demoModeEnabled = demoModeEnabled) {
            GoldenPathRouteHost(
                route = route,
                root = root,
                scope = scope,
                personaMode = personaMode,
                demoModeEnabled = demoModeEnabled,
                activeTransportSelection = activeTransportSelection,
                savedVehicleProfile = savedVehicleProfile,
                liveDataCoordinator = liveDataCoordinator,
                sessionHistoryCoordinator = sessionHistoryCoordinator,
                onRouteChange = { route = it },
                homeContent = {
                    GoldenPathScreen(
                        themeMode = themeMode,
                        isOnline = isOnline,
                        demoModeEnabled = demoModeEnabled,
                        personaMode = personaMode,
                        connectionStatus = connectionStatus,
                        vinDisplay = vinDisplay,
                        vinSourceLabel = vinSourceLabel,
                        savedVehicleProfile = savedVehicleProfile,
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
                        onPersonaChange = { mode -> scope.launch { root.personaPreferences.setPersona(mode) } },
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
                        bluetoothConnectEnabled = bluetoothConnectUi.canConnect,
                        bluetoothConnectConnecting = bluetoothConnectUi.isConnecting,
                        bluetoothLastAdapterLabel = bluetoothConnectUi.lastAdapterLabel,
                        bluetoothConnectStatusMessage = bluetoothConnectUi.statusMessage,
                        onBluetoothConnect = bluetoothConnectUi.onConnect,
                        onAboutOpen = { showAbout = !showAbout; if (showAbout) showSettings = false },
                        onAboutClose = { showAbout = false },
                        onSettingsOpen = { showSettings = !showSettings; if (showSettings) showAbout = false },
                        onSettingsClose = { showSettings = false },
                        onReviewPermissions = {
                            showSettings = false
                            showWelcomeReview = true
                        },
                        onUpdateCheckChange = { enabled ->
                            scope.launch {
                                appUpdatePreferences.setCheckInterval(
                                    SettingsLogic.intervalForToggle(enabled, checkInterval),
                                )
                            }
                        },
                        onApplyUpdate = updateUi.onApplyUpdate,
                        liveDataEnabled = connectionStatus.contains("Connected"),
                        onOpenLiveData = { route = GoldenPathRoute.LiveData },
                        onOpenSessionHistory = { route = GoldenPathRoute.SessionHistory },
                        onOpenVinResolve = { route = GoldenPathRoute.VinResolve },
                        onOpenShop = { route = GoldenPathRoute.Shop },
                        onOpenDtcExplain = { route = GoldenPathRoute.DtcExplain },
                        compositionRoot = root,
                        settingsScope = scope,
                    )
                },
            )
        }
    }
}
