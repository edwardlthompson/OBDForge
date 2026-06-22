package dev.foss.obdforge.data.demo

import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportMetrics
import dev.foss.obdforge.domain.transport.TransportType

class SimulatedObdTransport : ObdTransport {
    override val type: TransportType = TransportType.Simulated
    override val endpoint: TransportEndpoint = TransportEndpoint.Simulated
    override var state: ConnectionState = ConnectionState.Disconnected
        private set

    private var _metrics = TransportMetrics()
    override val metrics: TransportMetrics
        get() = _metrics

    override suspend fun connect(): Result<Unit> {
        val started = System.currentTimeMillis()
        state = ConnectionState.Connected
        _metrics = _metrics.copy(connectLatencyMs = System.currentTimeMillis() - started)
        return Result.success(Unit)
    }

    override suspend fun disconnect() {
        state = ConnectionState.Disconnected
    }

    override suspend fun write(line: String): Result<Unit> {
        if (state != ConnectionState.Connected) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        _metrics = _metrics.copy(bytesWritten = _metrics.bytesWritten + line.length)
        return Result.success(Unit)
    }

    override suspend fun read(timeoutMs: Long): Result<String> {
        if (state != ConnectionState.Connected) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        return Result.success("OK")
    }

    override suspend fun send(command: String): Result<String> {
        if (state != ConnectionState.Connected) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        val normalized = command.trim().uppercase()
        val response = when {
            normalized.contains("0902") || normalized == "0902" ->
                "49 02 01 31 47 31 4A 43 35 34 34 34 52 37 32 35 31 32 33 34"
            normalized.startsWith("03") -> "43 01 33 01 00 00 00 00 00"
            normalized.startsWith("010C") || normalized.contains("010C") -> "41 0C 0F A0"
            normalized.startsWith("ATZ") -> "ELM327 v2.3 (OBDForge Demo)"
            normalized.startsWith("ATI") -> "OBDForge Simulated Transport"
            else -> "OK"
        }
        _metrics = _metrics.copy(
            bytesWritten = _metrics.bytesWritten + command.length,
            bytesRead = _metrics.bytesRead + response.length,
        )
        return Result.success(response)
    }
}
