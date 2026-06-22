package dev.foss.obdforge.domain.protocol

import dev.foss.obdforge.domain.transport.ObdTransport

interface FastStreamingCapable {
    suspend fun readPidsBatched(
        transport: ObdTransport,
        mode: ObdMode,
        pids: List<Int>,
    ): Result<List<PidResponse>>
}
