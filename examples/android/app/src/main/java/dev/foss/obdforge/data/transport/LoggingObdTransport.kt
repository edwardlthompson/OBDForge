package dev.foss.obdforge.data.transport

import dev.foss.obdforge.data.diagnostics.DiagnosticEventRecorder
import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportMetrics
import dev.foss.obdforge.domain.transport.TransportType

class LoggingObdTransport(
    private val delegate: ObdTransport,
    private val recorder: DiagnosticEventRecorder,
) : ObdTransport {
    override val type: TransportType = delegate.type
    override val endpoint: TransportEndpoint = delegate.endpoint
    override val state: ConnectionState get() = delegate.state
    override val metrics: TransportMetrics get() = delegate.metrics

    override suspend fun connect(): Result<Unit> {
        val result = delegate.connect()
        result.fold(
            onSuccess = {
                recorder.recordConnection(
                    transportType = type,
                    success = true,
                    latencyMs = delegate.metrics.connectLatencyMs,
                )
            },
            onFailure = { error ->
                recorder.recordConnection(
                    transportType = type,
                    success = false,
                    errorMessage = error.message ?: error.javaClass.simpleName,
                )
            },
        )
        return result
    }

    override suspend fun disconnect() {
        delegate.disconnect()
        recorder.record(
            category = dev.foss.obdforge.domain.diagnostics.DiagnosticEventCategory.Connection,
            severity = dev.foss.obdforge.domain.diagnostics.DiagnosticEventSeverity.Info,
            message = "Disconnected",
            transportType = type,
        )
    }

    override suspend fun write(line: String): Result<Unit> =
        delegate.write(line).onFailure { error ->
            recorder.recordTransportIo(type, "Write", error.message ?: "I/O error")
        }

    override suspend fun read(timeoutMs: Long): Result<String> =
        delegate.read(timeoutMs).onFailure { error ->
            recorder.recordTransportIo(type, "Read", error.message ?: "I/O error")
        }

    override suspend fun send(command: String): Result<String> =
        delegate.send(command).onFailure { error ->
            recorder.recordTransportIo(type, "Send", error.message ?: "I/O error")
        }
}
