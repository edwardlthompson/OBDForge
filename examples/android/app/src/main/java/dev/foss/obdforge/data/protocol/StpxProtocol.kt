package dev.foss.obdforge.data.protocol

import dev.foss.obdforge.domain.protocol.BatchedObdResponseParser
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.DtcList
import dev.foss.obdforge.domain.protocol.FastStreamingCapable
import dev.foss.obdforge.domain.protocol.ObdIsoResponseParser
import dev.foss.obdforge.domain.protocol.ObdMode
import dev.foss.obdforge.domain.protocol.PidResponse
import dev.foss.obdforge.domain.protocol.ProbeResult
import dev.foss.obdforge.domain.protocol.ProtocolId
import dev.foss.obdforge.domain.protocol.StnResponseParser
import dev.foss.obdforge.domain.transport.ObdTransport

class StpxProtocol : DiagnosticProtocol, FastStreamingCapable {
    override val id: ProtocolId = ProtocolId.Stpx

    override suspend fun probe(transport: ObdTransport): ProbeResult {
        if (!StLinkInit.ensureConnected(transport)) return ProbeResult.Error
        if (StLinkInit.runBaseInit(transport).isFailure) return ProbeResult.Error
        val sti = StLinkInit.probeStnIdentity(transport).getOrNull() ?: return ProbeResult.Error
        if (!StnResponseParser.looksLikeStn(sti)) return ProbeResult.Unsupported
        val capabilities = StnResponseParser.parseCapabilities(
            stiResponse = sti,
            stixResponse = StLinkInit.probeStnExtendedIdentity(transport).getOrNull(),
        ) ?: return ProbeResult.Unsupported
        if (!capabilities.supportsStpx) return ProbeResult.Unsupported
        if (StLinkInit.setAutomaticProtocol(transport).isFailure) return ProbeResult.Error
        val stpxPing = transport.send("STPX d:0100").getOrNull() ?: return ProbeResult.Error
        return if (StnResponseParser.looksLikeStpxProbeResponse(stpxPing)) {
            ProbeResult.Supported
        } else {
            ProbeResult.Unsupported
        }
    }

    override suspend fun readPid(
        transport: ObdTransport,
        mode: ObdMode,
        pid: Int,
    ): Result<PidResponse> {
        if (!StLinkInit.ensureConnected(transport)) {
            return Result.failure(IllegalStateException("Transport not connected"))
        }
        return when (mode) {
            ObdMode.Mode01 -> readMode01ViaStpx(transport, pid)
            ObdMode.Mode09 -> StLinkObdCommands.readPid(transport, mode, pid)
            ObdMode.Mode03, ObdMode.Mode04 ->
                Result.failure(IllegalArgumentException("Use readDtcs/clearDtcs for mode $mode"))
        }
    }

    override suspend fun readPidsBatched(
        transport: ObdTransport,
        mode: ObdMode,
        pids: List<Int>,
    ): Result<List<PidResponse>> {
        if (!StLinkInit.ensureConnected(transport)) {
            return Result.failure(IllegalStateException("Transport not connected"))
        }
        if (pids.isEmpty()) return Result.success(emptyList())
        if (mode != ObdMode.Mode01) {
            return Result.failure(IllegalArgumentException("Batched reads support Mode 01 only"))
        }
        transport.send("STBC 1").getOrElse { return Result.failure(it) }
        transport.send("STBCOF 1").getOrElse { return Result.failure(it) }
        val batch = pids.joinToString("|") { StLinkObdCommands.buildObdCommand(mode, it) }
        val response = transport.send(batch).getOrElse { return Result.failure(it) }
        transport.send("STBC 0")
        val parsed = BatchedObdResponseParser.parseMode01Batch(response, pids)
        return if (parsed.size == pids.size) {
            Result.success(parsed)
        } else {
            Result.failure(IllegalStateException("Incomplete batched PID response"))
        }
    }

    override suspend fun readDtcs(transport: ObdTransport): Result<DtcList> =
        StLinkObdCommands.readDtcs(transport)

    override suspend fun clearDtcs(transport: ObdTransport): Result<Unit> =
        StLinkObdCommands.clearDtcs(transport)

    private suspend fun readMode01ViaStpx(
        transport: ObdTransport,
        pid: Int,
    ): Result<PidResponse> {
        val obd = StLinkObdCommands.buildObdCommand(ObdMode.Mode01, pid)
        val response = transport.send("STPX d:$obd").getOrElse { return Result.failure(it) }
        return ObdIsoResponseParser.parseMode01(response, pid)
            ?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("Invalid STPX Mode 01 response"))
    }
}
