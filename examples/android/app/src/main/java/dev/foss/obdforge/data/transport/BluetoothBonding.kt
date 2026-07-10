package dev.foss.obdforge.data.transport

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

enum class BluetoothBondState {
    Bonded,
    Bonding,
    None,
    Unavailable,
}

object BluetoothBonding {
    fun bondState(context: Context, address: String): BluetoothBondState {
        if (address.isBlank()) return BluetoothBondState.Unavailable
        if (!hasConnectPermission(context)) return BluetoothBondState.Unavailable
        return try {
            val device = remoteDevice(context, address) ?: return BluetoothBondState.Unavailable
            when (device.bondState) {
                BluetoothDevice.BOND_BONDED -> BluetoothBondState.Bonded
                BluetoothDevice.BOND_BONDING -> BluetoothBondState.Bonding
                else -> BluetoothBondState.None
            }
        } catch (_: SecurityException) {
            BluetoothBondState.Unavailable
        }
    }

    fun isBonded(context: Context, address: String): Boolean =
        bondState(context, address) == BluetoothBondState.Bonded

    /**
     * Starts system pairing. Returns true if already bonded or bond completed within timeout.
     */
    @SuppressLint("MissingPermission")
    suspend fun ensureBonded(
        context: Context,
        address: String,
        timeoutMs: Long = 30_000L,
    ): Boolean {
        if (address.isBlank()) return false
        if (!hasConnectPermission(context)) return false
        val device = remoteDevice(context, address) ?: return false
        if (device.bondState == BluetoothDevice.BOND_BONDED) return true
        val started = try {
            device.createBond()
        } catch (_: SecurityException) {
            return false
        }
        if (!started && device.bondState != BluetoothDevice.BOND_BONDED) {
            // createBond may return false if bonding already in progress — still wait.
        }
        val result = withTimeoutOrNull(timeoutMs) {
            awaitBonded(context, address)
        }
        return result == true || isBonded(context, address)
    }

    @SuppressLint("MissingPermission")
    private suspend fun awaitBonded(context: Context, address: String): Boolean =
        suspendCancellableCoroutine { cont ->
            val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    if (intent?.action != BluetoothDevice.ACTION_BOND_STATE_CHANGED) return
                    val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    } ?: return
                    if (!device.address.equals(address, ignoreCase = true)) return
                    when (device.bondState) {
                        BluetoothDevice.BOND_BONDED -> {
                            runCatching { context.unregisterReceiver(this) }
                            if (cont.isActive) cont.resume(true)
                        }
                        BluetoothDevice.BOND_NONE -> {
                            runCatching { context.unregisterReceiver(this) }
                            if (cont.isActive) cont.resume(false)
                        }
                    }
                }
            }
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            cont.invokeOnCancellation {
                runCatching { context.unregisterReceiver(receiver) }
            }
            if (isBonded(context, address) && cont.isActive) {
                runCatching { context.unregisterReceiver(receiver) }
                cont.resume(true)
            }
        }

    @SuppressLint("MissingPermission")
    private fun remoteDevice(context: Context, address: String): BluetoothDevice? {
        val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter ?: return null
        return try {
            adapter.getRemoteDevice(address)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private fun hasConnectPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
