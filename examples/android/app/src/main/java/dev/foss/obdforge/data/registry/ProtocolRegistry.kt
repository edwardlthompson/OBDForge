package dev.foss.obdforge.data.registry

import dev.foss.obdforge.data.protocol.Elm327Protocol
import dev.foss.obdforge.data.protocol.StnProtocol
import dev.foss.obdforge.data.protocol.StpxProtocol
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.ProbeResult
import dev.foss.obdforge.domain.transport.ObdTransport

class ProtocolRegistry {
    private val protocols = mutableListOf<DiagnosticProtocol>()

    fun register(protocol: DiagnosticProtocol) {
        protocols.add(protocol)
    }

    fun all(): List<DiagnosticProtocol> = protocols.toList()

    suspend fun selectBest(transport: ObdTransport): DiagnosticProtocol? =
        protocols.firstOrNull { it.probe(transport) == ProbeResult.Supported }

    companion object {
        fun default(): ProtocolRegistry =
            ProtocolRegistry().apply {
                register(StpxProtocol())
                register(StnProtocol())
                register(Elm327Protocol())
            }
    }
}
