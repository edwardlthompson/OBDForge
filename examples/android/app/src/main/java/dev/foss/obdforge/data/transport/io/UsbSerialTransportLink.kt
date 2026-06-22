package dev.foss.obdforge.data.transport.io

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsbSerialTransportLink(
    private val context: Context,
    private val deviceName: String,
    private val baudRate: Int,
) : TransportLink {
    private var port: UsbSerialPort? = null

    override val isOpen: Boolean
        get() = port != null

    override suspend fun open(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val device = usbManager.deviceList[deviceName]
                ?: error("USB device not found: $deviceName")
            requirePermission(usbManager, device)
            val driver = UsbSerialProber.getDefaultProber().probeDevice(device)
                ?: error("Unsupported USB serial device")
            val connection = usbManager.openDevice(device)
                ?: error("USB device not open — grant permission first")
            val serialPort = driver.ports.firstOrNull()
                ?: error("No serial port on USB device")
            serialPort.open(connection)
            serialPort.setParameters(baudRate, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            port = serialPort
        }
    }

    override suspend fun close() = withContext(Dispatchers.IO) {
        runCatching {
            port?.close()
        }
        port = null
    }

    override suspend fun write(bytes: ByteArray): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val active = requireNotNull(port) { "USB serial link not open" }
            active.write(bytes, WRITE_TIMEOUT_MS)
        }
    }

    override suspend fun readAvailable(timeoutMs: Long): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            val active = requireNotNull(port) { "USB serial link not open" }
            val buffer = ByteArray(512)
            val read = active.read(buffer, timeoutMs.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
            if (read <= 0) {
                ByteArray(0)
            } else {
                buffer.copyOf(read)
            }
        }
    }

    private fun requirePermission(usbManager: UsbManager, device: UsbDevice) {
        check(usbManager.hasPermission(device)) {
            "USB permission required for ${device.deviceName}"
        }
    }

    companion object {
        private const val WRITE_TIMEOUT_MS = 1_000
    }
}
