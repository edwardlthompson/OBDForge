package dev.foss.obdforge.data.demo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CannedSessionsTest {
    @Test
    fun exposesExactlyThreeSessions() {
        assertEquals(3, CannedSessions.all.size)
        assertEquals(
            setOf("idle_p0133", "highway_clean", "cold_pending"),
            CannedSessions.all.map { it.id }.toSet(),
        )
    }

    @Test
    fun byId_fallsBackToDefault() {
        assertEquals(CannedSessions.DEFAULT_ID, CannedSessions.byId("missing").id)
        assertTrue(CannedSessions.byId(CannedSessions.DEFAULT_ID).responses.containsKey("0902"))
    }
}
