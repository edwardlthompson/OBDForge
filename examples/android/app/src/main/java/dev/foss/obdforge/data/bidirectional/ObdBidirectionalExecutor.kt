package dev.foss.obdforge.data.bidirectional

import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.ObdIsoResponseParser
import dev.foss.obdforge.domain.transport.ObdTransport

class ObdBidirectionalExecutor(
    private val transportRegistry: TransportRegistry,
    private val protocolRegistry: ProtocolRegistry,
) {
    data class ActiveSession(
        val transport: ObdTransport,
        val protocol: DiagnosticProtocol,
    )

    suspend fun openSession(selection: TransportSelection): Result<ActiveSession> {
        val transport = transportRegistry.create(selection.type, selection.endpoint)
            ?: return Result.failure(IllegalStateException("Transport unavailable"))
        transport.connect().getOrElse { return Result.failure(it) }
        val protocol = protocolRegistry.selectBest(transport)
            ?: return Result.failure(IllegalStateException("No supported protocol"))
        return Result.success(ActiveSession(transport = transport, protocol = protocol))
    }

    suspend fun clearDtcs(session: ActiveSession): Result<String> {
        session.protocol.clearDtcs(session.transport).getOrElse { return Result.failure(it) }
        return Result.success("44")
    }

    suspend fun sendRaw(session: ActiveSession, command: String): Result<String> {
        val normalized = command.trim().uppercase()
        val response = session.transport.send(normalized).getOrElse { return Result.failure(it) }
        if (normalized == "04" && !ObdIsoResponseParser.parseMode04(response)) {
            return Result.failure(IllegalStateException("Invalid Mode 04 response"))
        }
        return Result.success(response)
    }

    suspend fun closeSession(session: ActiveSession) {
        session.transport.disconnect()
    }
}
