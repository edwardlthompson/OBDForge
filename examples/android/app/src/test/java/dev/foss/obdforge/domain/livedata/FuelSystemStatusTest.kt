package dev.foss.obdforge.domain.livedata

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FuelSystemStatusTest {
    @Test
    fun label_closedAndOpen() {
        assertEquals("Closed loop", FuelSystemStatus.label(2))
        assertEquals("Open loop (cold)", FuelSystemStatus.label(1))
        assertTrue(FuelSystemStatus.isClosedLoop(2))
        assertFalse(FuelSystemStatus.isClosedLoop(1))
    }

    @Test
    fun decodePacked_formatsBothBanks() {
        val packed = FuelSystemStatus.decodePacked(byteArrayOf(0x02, 0x02))!!
        assertEquals("Closed loop · B2: Closed loop", FuelSystemStatus.formatPacked(packed))
    }

    @Test
    fun decodePacked_bank1Only() {
        val packed = FuelSystemStatus.decodePacked(byteArrayOf(0x08))!!
        assertEquals("Open loop (fault)", FuelSystemStatus.formatPacked(packed))
    }
}
