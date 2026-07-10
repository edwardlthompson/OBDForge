package dev.foss.obdforge.data.transport.io

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import dev.foss.obdforge.domain.transport.BluetoothConnectException
import dev.foss.obdforge.domain.transport.BluetoothConnectFailure
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeoutException

internal object BluetoothSppConnect {
    val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    const val CONNECT_TIMEOUT_MS = 8_000L

    @SuppressLint("MissingPermission")
    fun openWithFallback(device: BluetoothDevice, timeoutMs: Long): BluetoothSocket {
        val secure = attempt(device, insecure = false, timeoutMs)
        if (secure.isSuccess) return secure.getOrThrow()
        val insecure = attempt(device, insecure = true, timeoutMs)
        if (insecure.isSuccess) return insecure.getOrThrow()
        throw mapOpenError(insecure.exceptionOrNull() ?: secure.exceptionOrNull()!!)
    }

    @SuppressLint("MissingPermission")
    private fun attempt(
        device: BluetoothDevice,
        insecure: Boolean,
        timeoutMs: Long,
    ): Result<BluetoothSocket> {
        val candidate = if (insecure) {
            device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
        } else {
            device.createRfcommSocketToServiceRecord(SPP_UUID)
        }
        return try {
            if (!connectWithTimeout(candidate, timeoutMs)) {
                runCatching { candidate.close() }
                Result.failure(TimeoutException("Bluetooth SPP connect timed out after ${timeoutMs}ms"))
            } else {
                Result.success(candidate)
            }
        } catch (error: Throwable) {
            runCatching { candidate.close() }
            Result.failure(error)
        }
    }

    private fun connectWithTimeout(candidate: BluetoothSocket, timeoutMs: Long): Boolean {
        val thread = Thread({
            try {
                candidate.connect()
            } catch (_: IOException) {
                // Surfaced via isConnected / close.
            }
        }, "obd-spp-connect")
        thread.start()
        thread.join(timeoutMs)
        if (thread.isAlive) {
            runCatching { candidate.close() }
            thread.join(500)
            return false
        }
        return candidate.isConnected
    }

    fun mapOpenError(error: Throwable): Throwable {
        if (error is BluetoothConnectException) return error
        if (error is SecurityException) {
            return BluetoothConnectException(
                BluetoothConnectFailure.PermissionDenied,
                "Bluetooth permission denied",
                error,
            )
        }
        if (error is TimeoutException) {
            return BluetoothConnectException(
                BluetoothConnectFailure.Timeout,
                error.message ?: "Bluetooth connect timed out",
                error,
            )
        }
        val message = error.message.orEmpty()
        val busy = message.contains("busy", ignoreCase = true) ||
            message.contains("refused", ignoreCase = true) ||
            message.contains("read failed", ignoreCase = true) ||
            message.contains("socket closed", ignoreCase = true) ||
            message.contains("connection reset", ignoreCase = true) ||
            error is IOException
        if (busy) {
            return BluetoothConnectException(
                BluetoothConnectFailure.BusyOrRefused,
                "Connection busy or refused — close the OBDLink app and retry",
                error,
            )
        }
        return error
    }
}
