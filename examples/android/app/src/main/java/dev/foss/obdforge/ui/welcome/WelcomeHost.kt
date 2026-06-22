package dev.foss.obdforge.ui.welcome

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import dev.foss.obdforge.ui.connect.UsbPermissionRequester

@Composable
fun WelcomeHost(
    activity: ComponentActivity?,
    onContinue: () -> Unit,
) {
    val context = LocalContext.current
    var bluetoothGranted by remember { mutableStateOf(WelcomePermissionCatalog.bluetoothGranted(context)) }
    var cameraGranted by remember { mutableStateOf(WelcomePermissionCatalog.cameraGranted(context)) }
    var usbDevices by remember { mutableStateOf(WelcomePermissionCatalog.usbDevices(context)) }

    fun refreshStates() {
        bluetoothGranted = WelcomePermissionCatalog.bluetoothGranted(context)
        cameraGranted = WelcomePermissionCatalog.cameraGranted(context)
        usbDevices = WelcomePermissionCatalog.usbDevices(context)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshStates()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val bluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { refreshStates() }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { refreshStates() }

    val usbRequester = remember(activity) {
        activity?.let { UsbPermissionRequester(it) }
    }

    WelcomeScreen(
        bluetoothRequired = WelcomePermissionCatalog.bluetoothRequired(),
        bluetoothGranted = bluetoothGranted,
        cameraGranted = cameraGranted,
        usbDevices = usbDevices,
        onGrantBluetooth = {
            bluetoothLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                ),
            )
        },
        onGrantCamera = {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        },
        onGrantUsb = { deviceName ->
            val requester = usbRequester ?: return@WelcomeScreen
            val device = requester.deviceForName(deviceName) ?: return@WelcomeScreen
            requester.requestPermission(device) { refreshStates() }
        },
        onContinue = onContinue,
    )
}
