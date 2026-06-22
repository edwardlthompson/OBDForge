package dev.foss.obdforge.data.protocol

import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.ProbeResult
import dev.foss.obdforge.domain.protocol.ProtocolId
import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.ObdTransport

/** Minimal ELM327 stub for Sprint 1 registry wiring; full plugin lands in Sprint 3. */
class StubElm327Protocol : DiagnosticProtocol {
    override val id: ProtocolId = ProtocolId.Elm327

    override suspend fun probe(transport: ObdTransport): ProbeResult {
        if (transport.state != ConnectionState.Connected) {
            transport.connect().getOrElse { return ProbeResult.Error }
        }
        val response = transport.send("ATI").getOrNull() ?: return ProbeResult.Error
        return if (response.contains("ELM", ignoreCase = true) || response.contains("OBDForge", ignoreCase = true)) {
            ProbeResult.Supported
        } else {
            ProbeResult.Unsupported
        }
    }
}
