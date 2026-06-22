package dev.foss.obdforge.data.demo

import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.Transport
import dev.foss.obdforge.domain.transport.TransportType

class SimulatedObdTransport : Transport {
    override val type: TransportType = TransportType.Simulated
    override var state: ConnectionState = ConnectionState.Disconnected
        private set

    override suspend fun connect(): Result<Unit> {
        state = ConnectionState.Connected
        return Result.success(Unit)
    }

    override suspend fun disconnect() {
        state = ConnectionState.Disconnected
    }

    override suspend fun send(command: String): Result<String> {
        if (state != ConnectionState.Connected) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        val normalized = command.trim().uppercase()
        return Result.success(
            when {
                normalized.contains("0902") || normalized == "0902" ->
                    "49 02 01 31 47 31 4A 43 35 34 34 34 52 37 32 35 31 32 33 34"
                normalized.startsWith("03") -> "43 01 33 01 00 00 00 00 00"
                normalized.startsWith("010C") || normalized.contains("010C") -> "41 0C 0F A0"
                normalized.startsWith("ATZ") -> "ELM327 v2.3 (OBDForge Demo)"
                normalized.startsWith("ATI") -> "OBDForge Simulated Transport"
                else -> "OK"
            },
        )
    }
}
