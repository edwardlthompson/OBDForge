package dev.foss.obdforge.ui.connect

import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VinReadResult
import dev.foss.obdforge.domain.vehicle.VinResolver

class ConnectDemoCoordinator(
    private val transportRegistry: TransportRegistry,
    private val protocolRegistry: ProtocolRegistry = ProtocolRegistry.default(),
    private val selection: TransportSelection = TransportSelection(
        type = TransportType.Simulated,
        endpoint = dev.foss.obdforge.domain.transport.TransportEndpoint.Simulated,
    ),
) {
    suspend fun connectAndReadVin(): VinReadResult {
        val transport = requireNotNull(
            transportRegistry.create(selection.type, selection.endpoint),
        ) {
            "Transport not registered: ${selection.type}"
        }
        transport.connect()
        protocolRegistry.selectBest(transport)
        return VinResolver.readFromEcu(transport) ?: VinResolver.demoVin()
    }

    suspend fun disconnect() {
        transportRegistry.create(selection.type, selection.endpoint)?.disconnect()
    }

    suspend fun selectedProtocol(): DiagnosticProtocol? {
        val transport = transportRegistry.create(selection.type, selection.endpoint) ?: return null
        if (transport.state != ConnectionState.Connected) {
            transport.connect()
        }
        return protocolRegistry.selectBest(transport)
    }
}
