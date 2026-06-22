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
            normalized == "ATZ" -> "ELM327 v2.3"
            normalized == "ATI" -> "ELM327 v2.3 (OBDForge Demo)"
            normalized == "ATE0" || normalized == "ATL0" || normalized == "ATSP0" -> "OK"
            normalized == "STBC 1" || normalized == "STBC 0" || normalized == "STBCOF 1" -> "OK"
            normalized.contains("0902") || normalized == "0902" ->
                "49 02 01 31 47 31 4A 43 35 34 34 34 52 37 32 35 31 32 33 34"
            normalized == "03" -> "43 01 33 00 00 00 00 00"
            normalized == "04" -> "44"
            normalized.contains("|") -> normalized.split("|").joinToString(" | ") { mode01Line(it) }
            normalized.startsWith("STPX D:") -> mode01Line(normalized.removePrefix("STPX D:"))
            else -> mode01Line(normalized)
        }
        _metrics = _metrics.copy(
            bytesWritten = _metrics.bytesWritten + command.length,
            bytesRead = _metrics.bytesRead + response.length,
        )
        return Result.success(response)
    }

    private fun mode01Line(command: String): String = when {
        command.contains("010C") -> "41 0C 0F A0"
        command.contains("010D") -> "41 0D 32"
        command.contains("0105") -> "41 05 7D"
        command.contains("0104") -> "41 04 50"
        command.contains("0111") -> "41 11 80"
        command.contains("010F") -> "41 0F 55"
        command.contains("012F") -> "41 2F 78"
        command.contains("011F") -> "41 1F 01 2C"
        command.contains("0142") -> "41 42 0E 74"
        command.contains("0146") -> "41 46 62"
        command.contains("015C") -> "41 5C 7A"
        command.contains("0100") -> "41 00 BE 1F A8 13"
        else -> "OK"
    }
}
