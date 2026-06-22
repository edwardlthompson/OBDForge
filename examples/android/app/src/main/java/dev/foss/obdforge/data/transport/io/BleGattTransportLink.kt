package dev.foss.obdforge.data.transport.io

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
class BleGattTransportLink(
    private val context: Context,
    private val adapter: BluetoothAdapter,
    private val deviceAddress: String,
) : TransportLink {
    private var gatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private val inbound = ConcurrentLinkedQueue<Byte>()

    override val isOpen: Boolean
        get() = gatt != null && writeCharacteristic != null

    override suspend fun open(): Result<Unit> = suspendCancellableCoroutine { cont ->
        val device = runCatching { adapter.getRemoteDevice(deviceAddress) }.getOrElse {
            cont.resume(Result.failure(it))
            return@suspendCancellableCoroutine
        }
        val callback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> gatt.discoverServices()
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        if (cont.isActive) {
                            cont.resume(Result.failure(IllegalStateException("BLE disconnected ($status)")))
                        }
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    if (cont.isActive) cont.resume(Result.failure(IllegalStateException("BLE service discovery failed")))
                    return
                }
                val profile = ObdBleUuids.PROFILES.firstNotNullOfOrNull { candidate ->
                    val service = gatt.getService(candidate.service) ?: return@firstNotNullOfOrNull null
                    val write = service.getCharacteristic(candidate.write) ?: return@firstNotNullOfOrNull null
                    val notify = service.getCharacteristic(candidate.notify) ?: return@firstNotNullOfOrNull null
                    Triple(write, notify, candidate)
                }
                if (profile == null) {
                    if (cont.isActive) cont.resume(Result.failure(IllegalStateException("No OBD BLE profile found")))
                    return
                }
                val (write, notify, _) = profile
                gatt.setCharacteristicNotification(notify, true)
                writeCharacteristic = write
                if (cont.isActive) cont.resume(Result.success(Unit))
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
            ) {
                characteristic.value?.forEach { inbound.add(it) }
            }
        }
        val created = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.connectGatt(context, false, callback, BluetoothDevice.TRANSPORT_LE)
        } else {
            device.connectGatt(context, false, callback)
        }
        gatt = created
        cont.invokeOnCancellation { created?.close() }
    }

    override suspend fun close() {
        gatt?.close()
        gatt = null
        writeCharacteristic = null
        inbound.clear()
    }

    override suspend fun write(bytes: ByteArray): Result<Unit> = runCatching {
        val characteristic = requireNotNull(writeCharacteristic) { "BLE link not open" }
        val activeGatt = requireNotNull(gatt) { "BLE link not open" }
        characteristic.value = bytes
        if (!activeGatt.writeCharacteristic(characteristic)) {
            error("BLE write failed")
        }
    }

    override suspend fun readAvailable(timeoutMs: Long): Result<ByteArray> {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (inbound.isNotEmpty()) {
                val chunk = ArrayList<Byte>()
                while (inbound.isNotEmpty()) {
                    inbound.poll()?.let(chunk::add)
                }
                return Result.success(chunk.toByteArray())
            }
            delay(10)
        }
        return Result.success(ByteArray(0))
    }
}
