package dev.foss.obdforge.data.transport.io

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import dev.foss.obdforge.domain.transport.BluetoothConnectException
import dev.foss.obdforge.domain.transport.BluetoothConnectFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

class BluetoothSppTransportLink(
    private val adapter: BluetoothAdapter,
    private val deviceAddress: String,
    private val connectTimeoutMs: Long = BluetoothSppConnect.CONNECT_TIMEOUT_MS,
) : TransportLink {
    private var socket: BluetoothSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null

    override val isOpen: Boolean
        get() = socket?.isConnected == true

    @SuppressLint("MissingPermission")
    override suspend fun open(): Result<Unit> = withContext(Dispatchers.IO) {
        if (deviceAddress.isBlank()) {
            return@withContext Result.failure(
                BluetoothConnectException(
                    BluetoothConnectFailure.EmptyAddress,
                    "Empty Bluetooth address",
                ),
            )
        }
        runCatching {
            val device = adapter.getRemoteDevice(deviceAddress)
            if (device.bondState != BluetoothDevice.BOND_BONDED) {
                throw BluetoothConnectException(
                    BluetoothConnectFailure.NotBonded,
                    "Adapter not paired — pair in Bluetooth settings first",
                )
            }
            adapter.cancelDiscovery()
            val connected = BluetoothSppConnect.openWithFallback(device, connectTimeoutMs)
            socket = connected
            input = connected.inputStream
            output = connected.outputStream
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { error -> Result.failure(BluetoothSppConnect.mapOpenError(error)) },
        )
    }

    override suspend fun close() = withContext(Dispatchers.IO) {
        closeQuietly()
    }

    private fun closeQuietly() {
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
            if (stream.available() <= 0) {
                val deadline = System.currentTimeMillis() + timeoutMs.coerceAtLeast(0)
                while (System.currentTimeMillis() < deadline) {
                    if (stream.available() > 0) break
                    Thread.sleep(20)
                }
                if (stream.available() <= 0) {
                    return@runCatching ByteArray(0)
                }
            }
            val read = stream.read(buffer)
            if (read <= 0) ByteArray(0) else buffer.copyOf(read)
        }
    }

    companion object {
        const val CONNECT_TIMEOUT_MS = BluetoothSppConnect.CONNECT_TIMEOUT_MS
        val SPP_UUID = BluetoothSppConnect.SPP_UUID
    }
}
