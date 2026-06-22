package dev.foss.obdforge.data.transport.io

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothSppTransportLink(
    private val adapter: BluetoothAdapter,
    private val deviceAddress: String,
) : TransportLink {
    private var socket: BluetoothSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null

    override val isOpen: Boolean
        get() = socket?.isConnected == true

    override suspend fun open(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val device = adapter.getRemoteDevice(deviceAddress)
            val active = device.createRfcommSocketToServiceRecord(SPP_UUID)
            adapter.cancelDiscovery()
            active.connect()
            socket = active
            input = active.inputStream
            output = active.outputStream
        }
    }

    override suspend fun close() = withContext(Dispatchers.IO) {
        runCatching {
            input?.close()
            output?.close()
            socket?.close()
        }
        input = null
        output = null
        socket = null
    }

    override suspend fun write(bytes: ByteArray): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val stream = requireNotNull(output) { "Bluetooth link not open" }
            stream.write(bytes)
            stream.flush()
        }
    }

    override suspend fun readAvailable(timeoutMs: Long): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            val stream = requireNotNull(input) { "Bluetooth link not open" }
            val buffer = ByteArray(512)
            val read = stream.read(buffer)
            if (read <= 0) {
                ByteArray(0)
            } else {
                buffer.copyOf(read)
            }
        }
    }

    companion object {
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}
