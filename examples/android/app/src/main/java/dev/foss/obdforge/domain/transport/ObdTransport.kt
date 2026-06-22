package dev.foss.obdforge.domain.transport

enum class TransportType {
    Bluetooth,
    UsbSerial,
    WiFi,
    Ethernet,
    Simulated,
}

enum class ConnectionState {
    Disconnected,
    Connecting,
    Connected,
    Error,
}

interface ObdTransport {
    val type: TransportType
    val endpoint: TransportEndpoint
    val state: ConnectionState
    val metrics: TransportMetrics

    suspend fun connect(): Result<Unit>
    suspend fun disconnect()
    suspend fun write(line: String): Result<Unit>
    suspend fun read(timeoutMs: Long = DEFAULT_READ_TIMEOUT_MS): Result<String>
    suspend fun send(command: String): Result<String>

    companion object {
        const val DEFAULT_READ_TIMEOUT_MS = 2_000L
    }
}

@Deprecated("Use ObdTransport", ReplaceWith("ObdTransport"))
typealias Transport = ObdTransport
