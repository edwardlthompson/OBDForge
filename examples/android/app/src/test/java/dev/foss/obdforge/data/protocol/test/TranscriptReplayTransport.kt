package dev.foss.obdforge.data.protocol.test

import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportMetrics
import dev.foss.obdforge.domain.transport.TransportType

class TranscriptReplayTransport(
    steps: List<TranscriptStep>,
) : ObdTransport {
    private val responses = steps.associate { normalize(it.command) to it.response }
    val commands = mutableListOf<String>()

    override val type: TransportType = TransportType.Simulated
    override val endpoint: TransportEndpoint = TransportEndpoint.Simulated
    override var state: ConnectionState = ConnectionState.Disconnected
    override val metrics: TransportMetrics = TransportMetrics()

    override suspend fun connect(): Result<Unit> {
        state = ConnectionState.Connected
        return Result.success(Unit)
    }

    override suspend fun disconnect() {
        state = ConnectionState.Disconnected
    }

    override suspend fun write(line: String): Result<Unit> = Result.success(Unit)

    override suspend fun read(timeoutMs: Long): Result<String> = Result.success("OK")

    override suspend fun send(command: String): Result<String> {
        commands.add(command)
        val key = normalize(command)
        return Result.success(responses[key] ?: "OK")
    }

    private fun normalize(command: String): String = command.trim().uppercase()
}
