package dev.foss.obdforge.domain.protocol

import dev.foss.obdforge.domain.transport.ObdTransport

enum class ProtocolId(val wireName: String) {
    Elm327("elm327"),
    Stn("stn"),
    Stpx("stpx"),
    J1939("j1939"),
}

enum class ProbeResult {
    Supported,
    Unsupported,
    Error,
}

interface DiagnosticProtocol {
    val id: ProtocolId

    suspend fun probe(transport: ObdTransport): ProbeResult

    suspend fun readPid(
        transport: ObdTransport,
        mode: ObdMode,
        pid: Int,
    ): Result<PidResponse>

    suspend fun readDtcs(transport: ObdTransport): Result<DtcList>

    suspend fun readPendingDtcs(transport: ObdTransport): Result<DtcList> =
        Result.failure(UnsupportedOperationException("Mode 07 not implemented"))

    suspend fun readFreezeFrame(
        transport: ObdTransport,
        pid: Int,
    ): Result<PidResponse> =
        Result.failure(UnsupportedOperationException("Mode 02 not implemented"))

    suspend fun clearDtcs(transport: ObdTransport): Result<Unit>
}
