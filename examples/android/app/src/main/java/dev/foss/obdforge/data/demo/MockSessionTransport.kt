package dev.foss.obdforge.data.demo

import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportMetrics
import dev.foss.obdforge.domain.transport.TransportType

/**
 * [ObdTransport] that replays a fixed [CannedSession]. Same interface as live transports;
 * intended for debug builds via the canned-session replay setting.
 */
class MockSessionTransport(
    private val session: CannedSession = CannedSessions.idleP0133,
) : ObdTransport {
    override val type: TransportType = TransportType.Simulated
    override val endpoint: TransportEndpoint = TransportEndpoint.Simulated
    override var state: ConnectionState = ConnectionState.Disconnected
        private set

    private var _metrics = TransportMetrics()
    override val metrics: TransportMetrics
        get() = _metrics

    val sessionId: String get() = session.id

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
        val response = session.responseFor(command)
        _metrics = _metrics.copy(
            bytesWritten = _metrics.bytesWritten + command.length,
            bytesRead = _metrics.bytesRead + response.length,
        )
        return Result.success(response)
    }
}
