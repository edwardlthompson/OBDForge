package dev.foss.goldenpath.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.goldenpath.BuildConfig
import dev.foss.goldenpath.R
import dev.foss.goldenpath.about.AppUpdatePreferences
import dev.foss.goldenpath.about.CheckSchedule
import dev.foss.goldenpath.about.DonationsLoader
import dev.foss.goldenpath.about.ReleaseAsset
import dev.foss.goldenpath.about.ReleaseAssetSelector
import dev.foss.goldenpath.about.ReleaseTagFetcher
import dev.foss.goldenpath.about.UpdateApplyCoordinator
import dev.foss.goldenpath.about.UpdateStatusEvaluator
import dev.foss.goldenpath.network.NetworkStatusMonitor
import dev.foss.goldenpath.settings.SettingsLogic
import dev.foss.goldenpath.ui.theme.GoldenPathTheme
import dev.foss.goldenpath.ui.theme.ThemeMode
import dev.foss.goldenpath.ui.theme.ThemePreferences
import dev.foss.goldenpath.ui.theme.next
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VinSourceType
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.ui.connect.ConnectDemoCoordinator
import dev.foss.obdforge.ui.livedata.LiveDataCoordinator
import dev.foss.obdforge.ui.livedata.LiveDataHost
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
    var updateStatus by remember { mutableStateOf(context.getString(R.string.about_update_current)) }
    var applyAsset by remember { mutableStateOf<ReleaseAsset?>(null) }
    val donations = remember { DonationsLoader.load(context) }
    val appVersion = BuildConfig.VERSION_NAME
    val activity = context as? ComponentActivity

    var showLiveData by remember { mutableStateOf(false) }
    var demoModeEnabled by remember { mutableStateOf(true) }
    var connectionStatus by remember {
        mutableStateOf(context.getString(R.string.connection_status_disconnected))
    }
    var vinDisplay by remember { mutableStateOf(context.getString(R.string.vin_unknown)) }
    var vinSourceLabel by remember { mutableStateOf("") }
    val root = compositionRoot ?: remember { ObdForgeCompositionRoot.create(context) }
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

    LaunchedEffect(demoModeEnabled) {
        if (!demoModeEnabled) {
            connectDemo.disconnect()
            connectionStatus = context.getString(R.string.connection_status_disconnected)
            vinDisplay = context.getString(R.string.vin_unknown)
            vinSourceLabel = ""
            return@LaunchedEffect
        }
        connectionStatus = context.getString(R.string.vin_reading)
        vinDisplay = context.getString(R.string.vin_unknown)
        val vinResult = connectDemo.connectAndReadVin()
        connectionStatus = context.getString(R.string.connection_status_connected)
        vinDisplay = context.getString(R.string.vin_label, vinResult.vin)
        vinSourceLabel = when (vinResult.source) {
            VinSourceType.EcuObd2 -> context.getString(R.string.vin_source_ecu)
            VinSourceType.Demo -> context.getString(R.string.vin_source_demo)
            else -> ""
        }
    }

    LaunchedEffect(pendingRestart) {
        if (pendingRestart) {
            updateStatus = context.getString(R.string.about_update_restarting)
        }
    }

    LaunchedEffect(checkInterval, lastChecked, isOnline, installedFormat, pendingRestart) {
        if (pendingRestart) return@LaunchedEffect
        if (!isOnline) return@LaunchedEffect
        if (!CheckSchedule.shouldCheck(checkInterval, lastChecked, System.currentTimeMillis())) return@LaunchedEffect
        val repo = ReleaseTagFetcher.loadReleaseRepo(context) ?: return@LaunchedEffect
        val release = ReleaseTagFetcher.fetchLatestRelease(repo) ?: return@LaunchedEffect
        val format = installedFormat ?: "apk"
        if (release.assets.isNotEmpty() && ReleaseAssetSelector.select(release.assets, format) == null) {
            updateStatus = context.getString(R.string.about_update_no_compatible)
            return@LaunchedEffect
        }
        appUpdatePreferences.setLastChecked(System.currentTimeMillis())
        val selected = ReleaseAssetSelector.select(release.assets, format)
        applyAsset = when (val result = UpdateStatusEvaluator.evaluate(appVersion, release.tag)) {
            is UpdateStatusEvaluator.Result.Current -> {
                updateStatus = context.getString(R.string.about_update_current)
                null
            }
            is UpdateStatusEvaluator.Result.Available -> {
                updateStatus = context.getString(R.string.about_update_available, result.version)
                selected
            }
        }
    }

    val canApplyUpdate = applyAsset != null

    GoldenPathTheme(themeMode = themeMode) {
        if (showLiveData) {
            LiveDataHost(
                coordinator = liveDataCoordinator,
                scope = scope,
                persona = personaMode,
                onPersonaChange = { mode -> scope.launch { root.personaPreferences.setPersona(mode) } },
                onBack = { showLiveData = false },
            )
        } else {
        GoldenPathScreen(
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
            updateStatus = updateStatus,
            donations = donations,
            canApplyUpdate = canApplyUpdate,
            onThemeToggle = { scope.launch { themePreferences.setThemeMode(themeMode.next()) } },
            onThemeModeSelect = { mode -> scope.launch { themePreferences.setThemeMode(mode) } },
            onDemoModeChange = { enabled -> demoModeEnabled = enabled },
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
            onApplyUpdate = {
                val asset = applyAsset ?: return@GoldenPathScreen
                val host = activity ?: return@GoldenPathScreen
                scope.launch {
                    UpdateApplyCoordinator.applySideloadUpdate(host, appUpdatePreferences, asset)
                }
            },
            liveDataEnabled = demoModeEnabled && connectionStatus.contains("Connected"),
            onOpenLiveData = { showLiveData = true },
        )
        }
    }
}
