package dev.foss.obdforge.data.transport

import dev.foss.obdforge.data.diagnostics.DiagnosticEventRecorder
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.preferences.TransportPreferences
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VinReadResult
import dev.foss.obdforge.domain.vehicle.VinResolver
import kotlinx.coroutines.flow.first

data class AdapterConnectOutcome(
    val selection: TransportSelection,
    val vinResult: VinReadResult,
)

object SavedTransportConnect {
    fun isReady(
        selection: TransportSelection,
        lastBluetooth: TransportEndpoint.Bluetooth? = null,
    ): Boolean = when (selection.type) {
        TransportType.Simulated -> false
        TransportType.Bluetooth -> {
            val address = (selection.endpoint as? TransportEndpoint.Bluetooth)?.deviceAddress
            !address.isNullOrBlank() || lastBluetooth != null
        }
        TransportType.UsbSerial -> {
            val name = (selection.endpoint as? TransportEndpoint.UsbSerial)?.deviceName
            !name.isNullOrBlank()
        }
        TransportType.WiFi, TransportType.Ethernet -> {
            val tcp = selection.endpoint as? TransportEndpoint.Tcp
            tcp != null && tcp.host.isNotBlank() && tcp.port > 0
        }
    }

    suspend fun resolveSelection(
        saved: TransportSelection,
        transportPreferences: TransportPreferences,
    ): TransportSelection? = resolveSelection(saved, transportPreferences.lastBluetoothEndpoint())

    fun resolveSelection(
        saved: TransportSelection,
        lastBluetooth: TransportEndpoint.Bluetooth?,
    ): TransportSelection? {
        if (saved.type == TransportType.Bluetooth) {
            val address = (saved.endpoint as? TransportEndpoint.Bluetooth)?.deviceAddress
            if (address.isNullOrBlank()) {
                val last = lastBluetooth ?: return null
                return TransportSelection(TransportType.Bluetooth, last)
            }
        }
        return saved.takeIf { isReady(saved, lastBluetooth) }
    }
}

class AdapterConnectUseCase(
    private val transportRegistry: TransportRegistry,
    private val protocolRegistry: ProtocolRegistry,
    private val transportPreferences: TransportPreferences,
    private val eventRecorder: DiagnosticEventRecorder? = null,
) {
    suspend fun connectSaved(): Result<AdapterConnectOutcome> {
        val saved = transportPreferences.selection.first()
        val lastBt = transportPreferences.lastBluetoothEndpoint()
        val selection = SavedTransportConnect.resolveSelection(saved, lastBt)
            ?: return Result.failure(IllegalStateException("No saved adapter"))
        val transport = transportRegistry.create(selection.type, selection.endpoint)
            ?: return Result.failure(IllegalStateException("Transport unavailable"))
        transport.connect().getOrElse { return Result.failure(it) }
        if (transport.state != ConnectionState.Connected) {
            return Result.failure(IllegalStateException("Adapter not connected"))
        }
        val protocol = protocolRegistry.selectBest(transport)
        if (protocol == null) {
            eventRecorder?.recordProtocolFailure(
                transportType = selection.type,
                message = "No supported protocol after connect",
            )
            return Result.failure(IllegalStateException("No supported protocol"))
        }
        val vinResult = VinResolver.readFromEcu(transport) ?: VinResolver.demoVin()
        transportPreferences.setSelection(selection.type, selection.endpoint)
        return Result.success(AdapterConnectOutcome(selection = selection, vinResult = vinResult))
    }
}
