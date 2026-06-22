package dev.foss.obdforge.ui.welcome

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.core.content.ContextCompat
import dev.foss.obdforge.data.transport.TransportDiscovery
import dev.foss.obdforge.ui.connect.BluetoothPermissionGate

data class WelcomeUsbDeviceState(
    val deviceName: String,
    val label: String,
    val granted: Boolean,
)

object WelcomePermissionCatalog {
    fun bluetoothRequired(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    fun bluetoothGranted(context: Context): Boolean =
        !bluetoothRequired() || BluetoothPermissionGate.hasAllPermissions(context)

    fun cameraGranted(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED

    fun usbDevices(context: Context): List<WelcomeUsbDeviceState> {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        return TransportDiscovery(context).attachedUsbSerialDevices().map { device ->
            val hardware = usbManager.deviceList[device.deviceName]
            WelcomeUsbDeviceState(
                deviceName = device.deviceName,
                label = "USB ${device.deviceName} (${device.vendorId}:${device.productId})",
                granted = hardware?.let(usbManager::hasPermission) ?: false,
            )
        }
    }
}
