package dev.foss.obdforge.data.protocol

import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.ObdTransport

internal object StLinkInit {
    suspend fun ensureConnected(transport: ObdTransport): Boolean {
        if (transport.state == ConnectionState.Connected) return true
        return transport.connect().isSuccess
    }

    suspend fun runBaseInit(transport: ObdTransport): Result<Unit> {
        for (command in listOf("ATZ", "ATE0", "ATL0")) {
            transport.send(command).getOrElse { return Result.failure(it) }
        }
        return Result.success(Unit)
    }

    suspend fun probeStnIdentity(transport: ObdTransport): Result<String> {
        return transport.send("STI").map { it.trim() }
    }

    suspend fun probeStnExtendedIdentity(transport: ObdTransport): Result<String> {
        return transport.send("STIX").map { it.trim() }
    }

    suspend fun setAutomaticProtocol(transport: ObdTransport): Result<Unit> {
        return transport.send("STP 0").map { }
    }
}
