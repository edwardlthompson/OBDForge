package dev.foss.obdforge.data.protocol

import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.DtcList
import dev.foss.obdforge.domain.protocol.ObdIsoResponseParser
import dev.foss.obdforge.domain.protocol.ObdMode
import dev.foss.obdforge.domain.protocol.PidResponse
import dev.foss.obdforge.domain.protocol.ProbeResult
import dev.foss.obdforge.domain.protocol.ProtocolId
import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.ObdTransport

class Elm327Protocol : DiagnosticProtocol {
    override val id: ProtocolId = ProtocolId.Elm327

    override suspend fun probe(transport: ObdTransport): ProbeResult {
        if (!ensureConnected(transport)) return ProbeResult.Error
        if (runInitSequence(transport).isFailure) return ProbeResult.Error
        val identify = transport.send("ATI").getOrNull() ?: return ProbeResult.Error
        return if (ObdIsoResponseParser.looksLikeElm327(identify)) {
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
        if (!ensureConnected(transport)) {
            return Result.failure(IllegalStateException("Transport not connected"))
        }
        val command = buildObdCommand(mode, pid)
        val response = transport.send(command).getOrElse { return Result.failure(it) }
        return when (mode) {
            ObdMode.Mode01 -> ObdIsoResponseParser.parseMode01(response, pid)
                ?.let { Result.success(it) }
                ?: Result.failure(IllegalStateException("Invalid Mode 01 response"))
            ObdMode.Mode02 -> ObdIsoResponseParser.parseMode02(response, pid)
                ?.let { Result.success(it) }
                ?: Result.failure(IllegalStateException("Invalid Mode 02 response"))
            ObdMode.Mode09 -> {
                val vin = ObdIsoResponseParser.parseMode09Vin(response)
                    ?: return Result.failure(IllegalStateException("Invalid Mode 09 response"))
                Result.success(
                    PidResponse(
                        mode = mode,
                        pid = pid,
                        payload = vin.toByteArray(Charsets.US_ASCII),
                        raw = response,
                    ),
                )
            }
            ObdMode.Mode03, ObdMode.Mode04, ObdMode.Mode07 ->
                Result.failure(IllegalArgumentException("Use readDtcs/readPendingDtcs/clearDtcs for mode $mode"))
        }
    }

    override suspend fun readDtcs(transport: ObdTransport): Result<DtcList> {
        if (!ensureConnected(transport)) {
            return Result.failure(IllegalStateException("Transport not connected"))
        }
        val response = transport.send("03").getOrElse { return Result.failure(it) }
        return ObdIsoResponseParser.parseMode03(response)
            ?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("Invalid Mode 03 response"))
    }

    override suspend fun readPendingDtcs(transport: ObdTransport): Result<DtcList> {
        if (!ensureConnected(transport)) {
            return Result.failure(IllegalStateException("Transport not connected"))
        }
        val response = transport.send("07").getOrElse { return Result.failure(it) }
        return ObdIsoResponseParser.parseMode07(response)
            ?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("Invalid Mode 07 response"))
    }

    override suspend fun readFreezeFrame(transport: ObdTransport, pid: Int): Result<PidResponse> =
        readPid(transport, ObdMode.Mode02, pid)

    override suspend fun clearDtcs(transport: ObdTransport): Result<Unit> {
        if (!ensureConnected(transport)) {
            return Result.failure(IllegalStateException("Transport not connected"))
        }
        val response = transport.send("04").getOrElse { return Result.failure(it) }
        return if (ObdIsoResponseParser.parseMode04(response)) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Invalid Mode 04 response"))
        }
    }

    private suspend fun runInitSequence(transport: ObdTransport): Result<Unit> {
        val steps = listOf("ATZ", "ATE0", "ATL0", "ATSP0")
        for (command in steps) {
            transport.send(command).getOrElse { return Result.failure(it) }
        }
        return Result.success(Unit)
    }

    private suspend fun ensureConnected(transport: ObdTransport): Boolean {
        if (transport.state == ConnectionState.Connected) return true
        return transport.connect().isSuccess
    }

    private fun buildObdCommand(mode: ObdMode, pid: Int): String =
        mode.wirePrefix + pid.toString(16).uppercase().padStart(2, '0')
}
