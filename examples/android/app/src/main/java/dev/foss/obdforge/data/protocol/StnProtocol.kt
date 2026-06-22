package dev.foss.obdforge.data.protocol

import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.DtcList
import dev.foss.obdforge.domain.protocol.ObdIsoResponseParser
import dev.foss.obdforge.domain.protocol.ObdMode
import dev.foss.obdforge.domain.protocol.PidResponse
import dev.foss.obdforge.domain.protocol.ProbeResult
import dev.foss.obdforge.domain.protocol.ProtocolId
import dev.foss.obdforge.domain.protocol.StnResponseParser
import dev.foss.obdforge.domain.transport.ObdTransport

class StnProtocol : DiagnosticProtocol {
    override val id: ProtocolId = ProtocolId.Stn

    override suspend fun probe(transport: ObdTransport): ProbeResult {
        if (!StLinkInit.ensureConnected(transport)) return ProbeResult.Error
        if (StLinkInit.runBaseInit(transport).isFailure) return ProbeResult.Error
        val sti = StLinkInit.probeStnIdentity(transport).getOrNull() ?: return ProbeResult.Error
        if (!StnResponseParser.looksLikeStn(sti)) return ProbeResult.Unsupported
        if (StLinkInit.setAutomaticProtocol(transport).isFailure) return ProbeResult.Error
        return ProbeResult.Supported
    }

    override suspend fun readPid(
        transport: ObdTransport,
        mode: ObdMode,
        pid: Int,
    ): Result<PidResponse> = StLinkObdCommands.readPid(transport, mode, pid)

    override suspend fun readDtcs(transport: ObdTransport): Result<DtcList> =
        StLinkObdCommands.readDtcs(transport)

    override suspend fun clearDtcs(transport: ObdTransport): Result<Unit> =
        StLinkObdCommands.clearDtcs(transport)
}
