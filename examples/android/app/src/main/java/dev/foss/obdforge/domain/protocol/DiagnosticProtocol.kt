package dev.foss.obdforge.domain.protocol

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
    suspend fun probe(transport: dev.foss.obdforge.domain.transport.Transport): ProbeResult
}
