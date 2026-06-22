package dev.foss.obdforge.data.transport

import dev.foss.obdforge.data.transport.io.FakeTransportLink
import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StreamObdTransportTest {
    @Test
    fun send_writesAndReadsLine() = runTest {
        val link = FakeTransportLink(listOf("ELM327 v2.3\r>"))
        val transport = StreamObdTransport(
            type = TransportType.WiFi,
            endpoint = TransportEndpoint.Tcp("127.0.0.1", 35000),
            link = link,
        )
        transport.connect()
        assertEquals(ConnectionState.Connected, transport.state)
        val response = transport.send("ATZ")
        assertTrue(response.isSuccess)
        assertEquals("ELM327 v2.3", response.getOrThrow())
        assertTrue(link.writes.single().startsWith("ATZ\r"))
        assertTrue(transport.metrics.bytesWritten > 0)
        assertTrue(transport.metrics.bytesRead > 0)
    }
}
