package dev.foss.goldenpath.ui

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.goldenpath.R
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VinSourceType
import dev.foss.obdforge.ui.connect.BluetoothPermissionGate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class BluetoothConnectUi(
    val lastAdapterLabel: String?,
    val isConnecting: Boolean,
    val canConnect: Boolean,
    val statusMessage: String,
    val onConnect: () -> Unit,
)

@Composable
fun rememberBluetoothConnectUi(
    context: Context,
    scope: CoroutineScope,
    root: ObdForgeCompositionRoot,
    demoModeEnabled: Boolean,
    onConnectionStatusChange: (String) -> Unit,
    onVinDisplayChange: (String) -> Unit,
    onVinSourceLabelChange: (String) -> Unit,
): BluetoothConnectUi {
    val lastBluetooth by root.transportPreferences.lastBluetooth.collectAsStateWithLifecycle(initialValue = null)
    var isConnecting by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    var pendingConnect by remember { mutableStateOf(false) }

    suspend fun performConnect() {
        val endpoint = root.transportPreferences.lastBluetoothEndpoint()
            ?: run {
                statusMessage = context.getString(R.string.bluetooth_connect_error_no_saved)
                return
            }
        isConnecting = true
        statusMessage = ""
        onConnectionStatusChange(context.getString(R.string.bluetooth_connecting))
        val result = root.bluetoothReconnectUseCase.connectLastKnown()
        isConnecting = false
        result.fold(
            onSuccess = { outcome ->
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
                    selection = TransportSelection(
                        type = TransportType.Bluetooth,
                        endpoint = endpoint,
                    ),
                    vinResult = outcome.vinResult,
                )
            },
            onFailure = { error ->
                statusMessage = context.getString(
                    R.string.bluetooth_connect_error_failed,
                    error.message ?: context.getString(R.string.bluetooth_connect_error_unknown),
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
        if (lastBluetooth == null) {
            statusMessage = context.getString(R.string.bluetooth_connect_error_no_saved)
            return
        }
        val missing = BluetoothPermissionGate.missingPermissions(context)
        if (missing.isNotEmpty()) {
            pendingConnect = true
            permissionLauncher.launch(missing.toTypedArray())
        } else {
            scope.launch { performConnect() }
        }
    }

    val label = lastBluetooth?.displayName ?: lastBluetooth?.deviceAddress
    return BluetoothConnectUi(
        lastAdapterLabel = label,
        isConnecting = isConnecting,
        canConnect = !demoModeEnabled && lastBluetooth != null,
        statusMessage = statusMessage,
        onConnect = ::requestConnect,
    )
}
