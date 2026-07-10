package dev.foss.obdforge.ui.shell

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.obdforge.R
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.transport.SavedTransportConnect
import dev.foss.obdforge.data.transport.displayLabel
import dev.foss.obdforge.domain.transport.BluetoothConnectFailure
import dev.foss.obdforge.domain.transport.BluetoothConnectFailures
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VinSourceType
import dev.foss.obdforge.ui.connect.BluetoothPermissionGate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class AdapterConnectUi(
    val isConnected: Boolean,
    val isConnecting: Boolean,
    val adapterSummary: String?,
    val transportLabel: String?,
    val canConnect: Boolean,
    val showSetupHint: Boolean,
    val statusMessage: String,
    val onConnect: () -> Unit,
)

@Composable
fun rememberAdapterConnectUi(
    context: Context,
    scope: CoroutineScope,
    root: ObdForgeCompositionRoot,
    demoModeEnabled: Boolean,
    savedTransport: TransportSelection,
    connectionStatus: String,
    onConnectionStatusChange: (String) -> Unit,
    onVinDisplayChange: (String) -> Unit,
    onVinSourceLabelChange: (String) -> Unit,
    onConnectedChange: (Boolean) -> Unit,
): AdapterConnectUi {
    val lastBluetooth by root.transportPreferences.lastBluetooth.collectAsStateWithLifecycle(initialValue = null)
    var isConnecting by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    var pendingConnect by remember { mutableStateOf(false) }

    val isReady = remember(savedTransport, lastBluetooth) {
        SavedTransportConnect.isReady(savedTransport, lastBluetooth)
    }
    val resolvedSelection = remember(savedTransport, lastBluetooth) {
        SavedTransportConnect.resolveSelection(savedTransport, lastBluetooth)
    }

    suspend fun performConnect() {
        if (!isReady) {
            statusMessage = context.getString(R.string.adapter_connect_error_no_saved)
            return
        }
        isConnecting = true
        statusMessage = ""
        onConnectedChange(false)
        onConnectionStatusChange(context.getString(R.string.bluetooth_connecting))
        val result = root.adapterConnectUseCase.connectSaved()
        isConnecting = false
        result.fold(
            onSuccess = { outcome ->
                onConnectedChange(true)
                onConnectionStatusChange(context.getString(R.string.connection_status_connected_adapter))
                onVinDisplayChange(context.getString(R.string.vin_label, outcome.vinResult.vin))
                onVinSourceLabelChange(
                    when (outcome.vinResult.source) {
                        VinSourceType.EcuObd2 -> context.getString(R.string.vin_source_ecu)
                        VinSourceType.Demo -> context.getString(R.string.vin_source_demo)
                        else -> ""
                    },
                )
                root.sessionRecorder.recordFromConnection(
                    selection = outcome.selection,
                    vinResult = outcome.vinResult,
                )
                root.vinProfileRepository.save(outcome.vinResult)
            },
            onFailure = { error ->
                onConnectedChange(false)
                statusMessage = bluetoothFailureMessage(context, error)
                root.diagnosticEventRecorder.record(
                    category = dev.foss.obdforge.domain.diagnostics.DiagnosticEventCategory.Connection,
                    severity = dev.foss.obdforge.domain.diagnostics.DiagnosticEventSeverity.Error,
                    message = "Adapter connect failed: ${error.message ?: "unknown"}",
                    transportType = savedTransport.type,
                )
                onConnectionStatusChange(context.getString(R.string.connection_status_disconnected))
            },
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        if (BluetoothPermissionGate.allGranted(results) && pendingConnect) {
            pendingConnect = false
            scope.launch { performConnect() }
        } else if (pendingConnect) {
            pendingConnect = false
            statusMessage = context.getString(R.string.bluetooth_connect_permission_denied)
        }
    }

    fun requestConnect() {
        if (demoModeEnabled || isConnecting) return
        if (!isReady) {
            statusMessage = context.getString(R.string.adapter_connect_error_no_saved)
            return
        }
        val needsBt = savedTransport.type == TransportType.Bluetooth
        if (needsBt) {
            val missing = BluetoothPermissionGate.missingPermissions(context)
            if (missing.isNotEmpty()) {
                pendingConnect = true
                permissionLauncher.launch(missing.toTypedArray())
                return
            }
        }
        scope.launch { performConnect() }
    }

    val transportLabel = transportTypeName(context, savedTransport.type)
    val adapterSummary = resolvedSelection?.endpoint?.displayLabel()
        ?: lastBluetooth?.displayLabel()

    return AdapterConnectUi(
        isConnected = connectionStatus.contains("Connected") && !demoModeEnabled,
        isConnecting = isConnecting,
        adapterSummary = adapterSummary,
        transportLabel = if (isReady) transportLabel else null,
        canConnect = !demoModeEnabled && isReady,
        showSetupHint = !demoModeEnabled && !isReady,
        statusMessage = statusMessage,
        onConnect = ::requestConnect,
    )
}

private fun transportTypeName(context: Context, type: TransportType): String =
    context.getString(
        when (type) {
            TransportType.Bluetooth -> R.string.transport_type_bluetooth
            TransportType.UsbSerial -> R.string.transport_type_usb
            TransportType.WiFi -> R.string.transport_type_wifi
            TransportType.Ethernet -> R.string.transport_type_ethernet
            TransportType.Simulated -> R.string.transport_type_simulated
        },
    )

internal fun bluetoothFailureMessage(context: Context, error: Throwable?): String {
    val kind = BluetoothConnectFailures.classify(error)
    val specific = when (kind) {
        BluetoothConnectFailure.EmptyAddress ->
            context.getString(R.string.bluetooth_connect_error_empty_address)
        BluetoothConnectFailure.NotBonded ->
            context.getString(R.string.bluetooth_connect_error_not_bonded)
        BluetoothConnectFailure.PermissionDenied ->
            context.getString(R.string.bluetooth_connect_permission_denied)
        BluetoothConnectFailure.BusyOrRefused ->
            context.getString(R.string.bluetooth_connect_error_busy)
        BluetoothConnectFailure.Timeout ->
            context.getString(R.string.bluetooth_connect_error_timeout)
        BluetoothConnectFailure.BleProfileMissing ->
            context.getString(R.string.bluetooth_connect_error_ble_profile)
        BluetoothConnectFailure.Unknown -> null
    }
    return specific ?: context.getString(
        R.string.bluetooth_connect_error_failed,
        error?.message ?: context.getString(R.string.bluetooth_connect_error_unknown),
    )
}
