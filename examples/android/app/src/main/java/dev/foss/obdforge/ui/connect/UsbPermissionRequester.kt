package dev.foss.obdforge.ui.connect

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class UsbPermissionRequester(
    private val activity: ComponentActivity,
) {
    private val usbManager = activity.getSystemService(Context.USB_SERVICE) as UsbManager

    fun requestPermission(
        device: UsbDevice,
        onResult: (Boolean) -> Unit,
    ) {
        if (usbManager.hasPermission(device)) {
            onResult(true)
            return
        }
        val action = "${activity.packageName}.USB_PERMISSION"
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action != action) return
                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                activity.unregisterReceiver(this)
                onResult(granted)
            }
        }
        ContextCompat.registerReceiver(
            activity,
            receiver,
            IntentFilter(action),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        val pendingIntent = PendingIntent.getBroadcast(
            activity,
            device.deviceId,
            Intent(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        usbManager.requestPermission(device, pendingIntent)
    }

    fun deviceForName(deviceName: String): UsbDevice? =
        usbManager.deviceList[deviceName]
}
