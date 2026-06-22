package dev.foss.obdforge.domain.transport

sealed interface TransportEndpoint {
    data object Simulated : TransportEndpoint

    data class Tcp(
        val host: String,
        val port: Int,
    ) : TransportEndpoint {
        companion object {
            const val DEFAULT_OBD_HOST = "192.168.0.10"
            const val DEFAULT_OBD_PORT = 35000
        }
    }

    data class Bluetooth(
        val deviceAddress: String,
        val displayName: String? = null,
    ) : TransportEndpoint

    data class UsbSerial(
        val deviceName: String,
        val baudRate: Int = DEFAULT_BAUD,
    ) : TransportEndpoint {
        companion object {
            const val DEFAULT_BAUD = 38400
        }
    }
}
