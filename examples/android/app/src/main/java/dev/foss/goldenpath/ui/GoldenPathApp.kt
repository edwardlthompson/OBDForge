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
import dev.foss.obdforge.domain.vehicle.VinSourceType
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.transport.BluetoothDeviceOption
import dev.foss.obdforge.data.transport.UsbDeviceOption
import dev.foss.obdforge.data.transport.displayLabel
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.ui.connect.ConnectDemoCoordinator
import dev.foss.obdforge.ui.connect.UsbPermissionRequester
import dev.foss.obdforge.ui.connect.buildEndpoint
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
    var pickerType by remember(savedTransport.type) { mutableStateOf(savedTransport.type) }
    var tcpHost by remember(savedTransport) {
        mutableStateOf((savedTransport.endpoint as? TransportEndpoint.Tcp)?.host.orEmpty())
    }
    var tcpPort by remember(savedTransport) {
        mutableStateOf(
            ((savedTransport.endpoint as? TransportEndpoint.Tcp)?.port
                ?: TransportEndpoint.Tcp.DEFAULT_OBD_PORT).toString(),
        )
    }
    var bluetoothAddress by remember(savedTransport) {
        mutableStateOf((savedTransport.endpoint as? TransportEndpoint.Bluetooth)?.deviceAddress.orEmpty())
    }
    var bluetoothName by remember(savedTransport) {
        mutableStateOf((savedTransport.endpoint as? TransportEndpoint.Bluetooth)?.displayName)
    }
    var usbDeviceName by remember(savedTransport) {
        mutableStateOf((savedTransport.endpoint as? TransportEndpoint.UsbSerial)?.deviceName.orEmpty())
    }
    var transportStatus by remember { mutableStateOf("") }
    var bluetoothDevices by remember { mutableStateOf(emptyList<BluetoothDeviceOption>()) }
    var usbDevices by remember { mutableStateOf(emptyList<UsbDeviceOption>()) }
    val usbPermissionRequester = remember(activity) {
        activity?.let { UsbPermissionRequester(it) }
    }
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

    LaunchedEffect(pickerType) {
        bluetoothDevices = if (pickerType == TransportType.Bluetooth) {
            root.transportDiscovery.pairedBluetoothDevices()
        } else {
            emptyList()
        }
        usbDevices = if (pickerType == TransportType.UsbSerial) {
            root.transportDiscovery.attachedUsbSerialDevices()
        } else {
            emptyList()
        }
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
            transportPickerType = pickerType,
            transportTcpHost = tcpHost,
            transportTcpPort = tcpPort,
            transportBluetoothAddress = bluetoothAddress,
            transportUsbDeviceName = usbDeviceName,
            bluetoothDevices = bluetoothDevices,
            usbDevices = usbDevices,
            transportStatusMessage = transportStatus,
            onTransportTypeChange = { pickerType = it },
            onTransportTcpHostChange = { tcpHost = it },
            onTransportTcpPortChange = { tcpPort = it },
            onBluetoothSelect = { device ->
                bluetoothAddress = device.address
                bluetoothName = device.name
            },
            onUsbSelect = { device -> usbDeviceName = device.deviceName },
            onSaveTransportSelection = {
                val endpoint = buildEndpoint(
                    type = pickerType,
                    tcpHost = tcpHost,
                    tcpPort = tcpPort,
                    bluetoothAddress = bluetoothAddress,
                    bluetoothName = bluetoothName,
                    usbDeviceName = usbDeviceName,
                )
                if (endpoint == null) {
                    transportStatus = context.getString(R.string.transport_status_invalid)
                } else {
                    scope.launch {
                        root.transportPreferences.setSelection(pickerType, endpoint)
                        transportStatus = context.getString(
                            R.string.transport_status_saved,
                            context.getString(
                                when (pickerType) {
                                    TransportType.Bluetooth -> R.string.transport_type_bluetooth
                                    TransportType.UsbSerial -> R.string.transport_type_usb
                                    TransportType.WiFi -> R.string.transport_type_wifi
                                    TransportType.Ethernet -> R.string.transport_type_ethernet
                                    TransportType.Simulated -> R.string.transport_type_simulated
                                },
                            ),
                            endpoint.displayLabel(),
                        )
                        connectionStatus = context.getString(R.string.connection_status_adapter_ready)
                    }
                }
            },
            onRequestUsbPermission = {
                val requester = usbPermissionRequester ?: return@GoldenPathScreen
                val device = requester.deviceForName(usbDeviceName) ?: return@GoldenPathScreen
                requester.requestPermission(device) { granted ->
                    transportStatus = context.getString(
                        if (granted) R.string.transport_status_usb_granted else R.string.transport_status_usb_denied,
                    )
                }
            },
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
        )
    }
}
