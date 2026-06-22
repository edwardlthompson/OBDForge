package dev.foss.obdforge.domain.protocol

import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportMetrics
import dev.foss.obdforge.domain.transport.TransportType
import org.junit.Assert.assertEquals
import org.junit.Test

class DiagnosticProtocolContractTest {
    @Test
    fun protocolId_wireNamesAreStable() {
        assertEquals("elm327", ProtocolId.Elm327.wireName)
        assertEquals("stn", ProtocolId.Stn.wireName)
        assertEquals("stpx", ProtocolId.Stpx.wireName)
    }

    private class FakeTransport : ObdTransport {
        override val type = TransportType.Simulated
        override val endpoint = TransportEndpoint.Simulated
        override var state = ConnectionState.Disconnected
        override val metrics = TransportMetrics()
        override suspend fun connect(): Result<Unit> {
            state = ConnectionState.Connected
            return Result.success(Unit)
        }
        override suspend fun disconnect() {
            state = ConnectionState.Disconnected
        }
        override suspend fun write(line: String) = Result.success(Unit)
        override suspend fun read(timeoutMs: Long) = Result.success("OK")
        override suspend fun send(command: String) = Result.success("OK")
    }
}
