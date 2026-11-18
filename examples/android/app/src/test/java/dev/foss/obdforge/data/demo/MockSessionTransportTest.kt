package dev.foss.obdforge.data.demo

import dev.foss.obdforge.domain.transport.ConnectionState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockSessionTransportTest {
    @Test
    fun replaysAllThreeCannedSessions() = runTest {
        for (session in CannedSessions.all) {
            val transport = MockSessionTransport(session)
            assertTrue(transport.connect().isSuccess)
            assertEquals(ConnectionState.Connected, transport.state)
            assertEquals(session.id, transport.sessionId)
            val ati = transport.send("ATI").getOrThrow()
            assertTrue(ati.contains("canned") || ati.isNotBlank())
            val vin = transport.send("0902").getOrThrow()
            assertTrue(vin.startsWith("49"))
            transport.disconnect()
            assertEquals(ConnectionState.Disconnected, transport.state)
        }
    }

    @Test
    fun idleSession_returnsP0133StoredDtc() = runTest {
        val transport = MockSessionTransport(CannedSessions.idleP0133)
        assertTrue(transport.connect().isSuccess)
        val stored = transport.send("03").getOrThrow()
        assertTrue(stored.contains("01 33") || stored.contains("0133"))
    }

    @Test
    fun highwaySession_reportsNoStoredDtcs() = runTest {
        val transport = MockSessionTransport(CannedSessions.highwayClean)
        assertTrue(transport.connect().isSuccess)
        assertEquals("43 00", transport.send("03").getOrThrow())
    }

    @Test
    fun coldSession_reportsPendingDtcs() = runTest {
        val transport = MockSessionTransport(CannedSessions.coldStartPending)
        assertTrue(transport.connect().isSuccess)
        val pending = transport.send("07").getOrThrow()
        assertTrue(pending.startsWith("47"))
        assertTrue(pending != "47 00")
    }
}
