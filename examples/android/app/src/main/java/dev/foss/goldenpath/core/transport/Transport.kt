package dev.foss.goldenpath.core.transport

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

interface Transport {
    val type: TransportType
    val state: ConnectionState
    suspend fun connect(): Result<Unit>
    suspend fun disconnect()
    suspend fun send(command: String): Result<String>
}
