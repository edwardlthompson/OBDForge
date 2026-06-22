package dev.foss.obdforge.domain.protocol

import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.Transport
import dev.foss.obdforge.domain.transport.TransportType
import org.junit.Assert.assertEquals
import org.junit.Test

class DiagnosticProtocolContractTest {
    @Test
    fun protocolId_wireNamesAreStable() {
        assertEquals("elm327", ProtocolId.Elm327.wireName)
        assertEquals("stn", ProtocolId.Stn.wireName)
    }

    private class FakeTransport : Transport {
        override val type = TransportType.Simulated
        override var state = ConnectionState.Disconnected
        override suspend fun connect(): Result<Unit> {
            state = ConnectionState.Connected
            return Result.success(Unit)
        }
        override suspend fun disconnect() {
            state = ConnectionState.Disconnected
        }
        override suspend fun send(command: String) = Result.success("OK")
    }
}
