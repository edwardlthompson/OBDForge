package dev.foss.obdforge.ui.connect

import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VinReadResult
import dev.foss.obdforge.domain.vehicle.VinResolver

class ConnectDemoCoordinator(
    private val transportRegistry: TransportRegistry = TransportRegistry.default(),
    private val protocolRegistry: ProtocolRegistry = ProtocolRegistry.default(),
    private val transportType: TransportType = TransportType.Simulated,
) {
    suspend fun connectAndReadVin(): VinReadResult {
        val transport = requireNotNull(transportRegistry.create(transportType)) {
            "Transport not registered: $transportType"
        }
        transport.connect()
        protocolRegistry.selectBest(transport)
        return VinResolver.readFromEcu(transport) ?: VinResolver.demoVin()
    }

    suspend fun disconnect() {
        transportRegistry.create(transportType)?.disconnect()
    }

    suspend fun selectedProtocol(): DiagnosticProtocol? {
        val transport = transportRegistry.create(transportType) ?: return null
        if (transport.state != ConnectionState.Connected) {
            transport.connect()
        }
        return protocolRegistry.selectBest(transport)
    }
}
