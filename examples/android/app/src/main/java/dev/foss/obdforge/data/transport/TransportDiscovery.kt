package dev.foss.obdforge.data.transport

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.hoho.android.usbserial.driver.UsbSerialProber
import dev.foss.obdforge.domain.transport.TransportEndpoint
import kotlinx.coroutines.delay

data class BluetoothDeviceOption(
    val address: String,
    val name: String?,
)

data class UsbDeviceOption(
    val deviceName: String,
    val vendorId: Int,
    val productId: Int,
)

class TransportDiscovery(private val context: Context) {
    fun pairedBluetoothDevices(): List<BluetoothDeviceOption> {
        if (!hasBluetoothConnectPermission()) return emptyList()
        val manager = context.getSystemService(BluetoothManager::class.java) ?: return emptyList()
        val adapter = manager.adapter ?: return emptyList()
        if (!adapter.isEnabled) return emptyList()
        return try {
            adapter.bondedDevices.orEmpty().map { device ->
                BluetoothDeviceOption(
                    address = device.address,
                    name = device.name,
                )
            }.sortedBy { it.name ?: it.address }
        } catch (_: SecurityException) {
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun discoverBluetoothDevices(scanDurationMs: Long = 4000L): List<BluetoothDeviceOption> {
        if (!hasBluetoothConnectPermission()) return emptyList()
        val devices = pairedBluetoothDevices().associateBy { it.address }.toMutableMap()
        val manager = context.getSystemService(BluetoothManager::class.java) ?: return devices.values.toSortedList()
        val adapter = manager.adapter ?: return devices.values.toSortedList()
        if (!adapter.isEnabled) return devices.values.toSortedList()
        val scanner = adapter.bluetoothLeScanner ?: return devices.values.toSortedList()
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device ?: return
                devices.getOrPut(device.address) {
                    BluetoothDeviceOption(
                        address = device.address,
                        name = device.name ?: result.scanRecord?.deviceName,
                    )
                }
            }
        }
        return try {
            scanner.startScan(callback)
            delay(scanDurationMs)
            devices.values.toSortedList()
        } finally {
            scanner.stopScan(callback)
        }
    }

    private fun Collection<BluetoothDeviceOption>.toSortedList(): List<BluetoothDeviceOption> =
        sortedBy { it.name ?: it.address }

    fun attachedUsbSerialDevices(): List<UsbDeviceOption> {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        return usbManager.deviceList.values.mapNotNull { device ->
            val driver = UsbSerialProber.getDefaultProber().probeDevice(device) ?: return@mapNotNull null
            if (driver.ports.isEmpty()) return@mapNotNull null
            UsbDeviceOption(
                deviceName = device.deviceName,
                vendorId = device.vendorId,
                productId = device.productId,
            )
        }.sortedBy { it.deviceName }
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT,
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun TransportEndpoint.displayLabel(): String =
    when (this) {
        TransportEndpoint.Simulated -> "simulated"
        is TransportEndpoint.Tcp -> "$host:$port"
        is TransportEndpoint.Bluetooth -> displayName ?: deviceAddress
        is TransportEndpoint.UsbSerial -> deviceName
    }
