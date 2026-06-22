package dev.foss.obdforge.data.registry

import dev.foss.obdforge.data.protocol.StubElm327Protocol
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.ProbeResult
import dev.foss.obdforge.domain.transport.Transport

class ProtocolRegistry {
    private val protocols = mutableListOf<DiagnosticProtocol>()

    fun register(protocol: DiagnosticProtocol) {
        protocols.add(protocol)
    }

    fun all(): List<DiagnosticProtocol> = protocols.toList()

    suspend fun selectBest(transport: Transport): DiagnosticProtocol? =
        protocols.firstOrNull { it.probe(transport) == ProbeResult.Supported }

    companion object {
        fun default(): ProtocolRegistry =
            ProtocolRegistry().apply {
                register(StubElm327Protocol())
            }
    }
}
