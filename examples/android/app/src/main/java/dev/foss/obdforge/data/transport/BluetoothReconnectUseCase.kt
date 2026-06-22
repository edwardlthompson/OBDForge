package dev.foss.obdforge.data.transport

import android.content.Context
import dev.foss.obdforge.data.diagnostics.DiagnosticEventRecorder
import dev.foss.obdforge.data.preferences.TransportPreferences
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.transport.BluetoothLinkKind
import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VinReadResult
import dev.foss.obdforge.domain.vehicle.VinResolver

data class BluetoothConnectOutcome(
    val vinResult: VinReadResult,
    val linkKind: BluetoothLinkKind,
)

class BluetoothReconnectUseCase(
    private val transportRegistry: TransportRegistry,
    private val protocolRegistry: ProtocolRegistry,
    private val transportPreferences: TransportPreferences,
    private val eventRecorder: DiagnosticEventRecorder? = null,
) {
    suspend fun connectLastKnown(): Result<BluetoothConnectOutcome> {
        val endpoint = transportPreferences.lastBluetoothEndpoint()
            ?: return Result.failure(IllegalStateException("No saved Bluetooth adapter"))
        val selection = dev.foss.obdforge.data.preferences.TransportSelection(
            type = TransportType.Bluetooth,
            endpoint = endpoint,
        )
        val transport = transportRegistry.create(TransportType.Bluetooth, endpoint)
            ?: return Result.failure(IllegalStateException("Bluetooth transport unavailable"))
        transport.connect().getOrElse { return Result.failure(it) }
        if (transport.state != ConnectionState.Connected) {
            return Result.failure(IllegalStateException("Adapter not connected"))
        }
        val protocol = protocolRegistry.selectBest(transport)
        if (protocol == null) {
            eventRecorder?.recordProtocolFailure(
                transportType = TransportType.Bluetooth,
                message = "No supported protocol after Bluetooth connect",
            )
            return Result.failure(IllegalStateException("No supported protocol"))
        }
        val vinResult = VinResolver.readFromEcu(transport) ?: VinResolver.demoVin()
        transportPreferences.setSelection(TransportType.Bluetooth, endpoint)
        return Result.success(
            BluetoothConnectOutcome(vinResult = vinResult, linkKind = endpoint.linkKind),
        )
    }

    suspend fun disconnect() {
        transportPreferences.lastBluetoothEndpoint()?.let { endpoint ->
            transportRegistry.create(TransportType.Bluetooth, endpoint)?.disconnect()
        }
    }
}
