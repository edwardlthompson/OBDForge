package dev.foss.obdforge.domain.transport

/**
 * Classifies Bluetooth connect failures for actionable UI copy.
 */
enum class BluetoothConnectFailure {
    EmptyAddress,
    NotBonded,
    PermissionDenied,
    BusyOrRefused,
    Timeout,
    BleProfileMissing,
    Unknown,
}

object BluetoothConnectFailures {
    fun classify(error: Throwable?): BluetoothConnectFailure {
        if (error is BluetoothConnectException) return error.kind
        val message = error?.message.orEmpty()
        return when {
            error is SecurityException -> BluetoothConnectFailure.PermissionDenied
            message.contains("empty address", ignoreCase = true) -> BluetoothConnectFailure.EmptyAddress
            message.contains("not bonded", ignoreCase = true) ||
                message.contains("not paired", ignoreCase = true) -> BluetoothConnectFailure.NotBonded
            message.contains("permission", ignoreCase = true) -> BluetoothConnectFailure.PermissionDenied
            message.contains("busy", ignoreCase = true) ||
                message.contains("refused", ignoreCase = true) ||
                message.contains("read failed", ignoreCase = true) ||
                message.contains("socket closed", ignoreCase = true) ||
                message.contains("connection reset", ignoreCase = true) -> BluetoothConnectFailure.BusyOrRefused
            message.contains("timeout", ignoreCase = true) ||
                message.contains("timed out", ignoreCase = true) -> BluetoothConnectFailure.Timeout
            message.contains("no obd ble", ignoreCase = true) ||
                message.contains("ble profile", ignoreCase = true) -> BluetoothConnectFailure.BleProfileMissing
            else -> BluetoothConnectFailure.Unknown
        }
    }
}

class BluetoothConnectException(
    val kind: BluetoothConnectFailure,
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
