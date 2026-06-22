package dev.foss.obdforge.domain.livedata

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveDataLayoutResolverTest {
    @Test
    fun diyLayout_usesLargeCardsAndFewerPids() {
        val layout = LiveDataLayoutResolver.resolve(PersonaMode.Diy)
        assertFalse(layout.compact)
        assertEquals(2, layout.columns)
        assertEquals(500L, layout.pollIntervalMs)
        assertEquals(4, layout.pids.size)
    }

    @Test
    fun racingLayout_usesCompactGridAndHighRate() {
        val layout = LiveDataLayoutResolver.resolve(PersonaMode.Racing)
        assertTrue(layout.compact)
        assertEquals(4, layout.columns)
        assertEquals(100L, layout.pollIntervalMs)
        assertTrue(layout.pids.size >= 10)
    }
}
