package dev.foss.goldenpath.core.demo

import dev.foss.goldenpath.core.transport.ConnectionState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulatedObdTransportTest {
    @Test
    fun connectAndReadVinMode09() = runTest {
        val transport = SimulatedObdTransport()
        assertTrue(transport.connect().isSuccess)
        assertEquals(ConnectionState.Connected, transport.state)
        val response = transport.send("0902").getOrThrow()
        assertTrue(response.contains("49"))
        transport.disconnect()
        assertEquals(ConnectionState.Disconnected, transport.state)
    }
}
