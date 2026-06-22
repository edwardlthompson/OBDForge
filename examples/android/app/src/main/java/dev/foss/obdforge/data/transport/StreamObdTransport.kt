package dev.foss.obdforge.data.transport

import dev.foss.obdforge.data.transport.io.TransportLink
import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportMetrics
import dev.foss.obdforge.domain.transport.TransportType

class StreamObdTransport(
    override val type: TransportType,
    override val endpoint: TransportEndpoint,
    private val link: TransportLink,
) : ObdTransport {
    override var state: ConnectionState = ConnectionState.Disconnected
        private set

    private var _metrics = TransportMetrics()
    override val metrics: TransportMetrics
        get() = _metrics

    private val readBuffer = StringBuilder()

    override suspend fun connect(): Result<Unit> {
        state = ConnectionState.Connecting
        val started = System.currentTimeMillis()
        return link.open().fold(
            onSuccess = {
                _metrics = _metrics.copy(connectLatencyMs = System.currentTimeMillis() - started)
                state = ConnectionState.Connected
                Result.success(Unit)
            },
            onFailure = {
                state = ConnectionState.Error
                Result.failure(it)
            },
        )
    }

    override suspend fun disconnect() {
        link.close()
        readBuffer.clear()
        state = ConnectionState.Disconnected
    }

    override suspend fun write(line: String): Result<Unit> {
        if (state != ConnectionState.Connected) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        val payload = ObdLineCodec.encode(line)
        return link.write(payload).onSuccess {
            _metrics = _metrics.copy(bytesWritten = _metrics.bytesWritten + payload.size)
        }
    }

    override suspend fun read(timeoutMs: Long): Result<String> {
        if (state != ConnectionState.Connected) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        val deadline = System.currentTimeMillis() + timeoutMs
        val lines = mutableListOf<String>()
        while (System.currentTimeMillis() < deadline) {
            val remaining = deadline - System.currentTimeMillis()
            if (remaining <= 0) break
            val chunk = link.readAvailable(remaining).getOrElse { return Result.failure(it) }
            if (chunk.isNotEmpty()) {
                _metrics = _metrics.copy(bytesRead = _metrics.bytesRead + chunk.size)
                val text = chunk.toString(Charsets.US_ASCII)
                lines.addAll(ObdLineCodec.drainLines(readBuffer, text))
                if (text.contains('>') || lines.isNotEmpty()) {
                    return Result.success(ObdLineCodec.responseFromLines(lines))
                }
            }
        }
        return if (lines.isNotEmpty()) {
            Result.success(ObdLineCodec.responseFromLines(lines))
        } else {
            Result.failure(java.util.concurrent.TimeoutException("No adapter response"))
        }
    }

    override suspend fun send(command: String): Result<String> {
        write(command).getOrElse { return Result.failure(it) }
        return read()
    }
}
