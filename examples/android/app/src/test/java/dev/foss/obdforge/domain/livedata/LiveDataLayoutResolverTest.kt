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
        assertEquals(7, layout.pids.size)
        assertTrue(layout.pids.contains(0x03))
        assertTrue(layout.pids.contains(0x14))
        assertTrue(layout.pids.contains(0x24))
    }

    @Test
    fun semiProLayout_usesMediumDensity() {
        val layout = LiveDataLayoutResolver.resolve(PersonaMode.SemiPro)
        assertFalse(layout.compact)
        assertEquals(3, layout.columns)
        assertTrue(layout.pids.size >= 6)
        assertTrue(layout.pids.contains(0x14))
    }

    @Test
    fun shopLayout_usesCompactGrid() {
        val layout = LiveDataLayoutResolver.resolve(PersonaMode.Shop)
        assertTrue(layout.compact)
        assertEquals(4, layout.columns)
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
