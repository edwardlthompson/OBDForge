package dev.foss.obdforge.data.transport

import android.bluetooth.BluetoothManager
import android.content.Context
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialProber
import dev.foss.obdforge.domain.transport.TransportEndpoint

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
        val manager = context.getSystemService(BluetoothManager::class.java) ?: return emptyList()
        val adapter = manager.adapter ?: return emptyList()
        if (!adapter.isEnabled) return emptyList()
        return adapter.bondedDevices.orEmpty().map { device ->
            BluetoothDeviceOption(
                address = device.address,
                name = device.name,
            )
        }.sortedBy { it.name ?: it.address }
    }

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
}

fun TransportEndpoint.displayLabel(): String =
    when (this) {
        TransportEndpoint.Simulated -> "simulated"
        is TransportEndpoint.Tcp -> "$host:$port"
        is TransportEndpoint.Bluetooth -> displayName ?: deviceAddress
        is TransportEndpoint.UsbSerial -> deviceName
    }
