package dev.foss.obdforge.data.transport

import dev.foss.obdforge.data.demo.CannedSessions
import dev.foss.obdforge.data.demo.MockSessionTransport
import dev.foss.obdforge.data.demo.SimulatedObdTransport
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulatedTransportSelectorTest {
    @Test
    fun defaultsToSimulatedObdTransport() {
        val selector = SimulatedTransportSelector()
        assertTrue(selector.create() is SimulatedObdTransport)
    }

    @Test
    fun usesMockSessionWhenEnabled() {
        val selector = SimulatedTransportSelector().apply {
            useMockTransport = true
            mockSessionId = CannedSessions.highwayClean.id
        }
        val transport = selector.create()
        assertTrue(transport is MockSessionTransport)
        assertTrue((transport as MockSessionTransport).sessionId == CannedSessions.highwayClean.id)
    }
}
