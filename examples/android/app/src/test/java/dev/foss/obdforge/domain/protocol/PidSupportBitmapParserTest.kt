package dev.foss.obdforge.domain.protocol

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PidSupportBitmapParserTest {
    @Test
    fun parseSupportedPids_demoFixtureBitmap() {
        val supported = PidSupportBitmapParser.parseSupportedPids("41 00 BE 1F A8 13", 0x00)
        assertTrue(0x0C in supported)
        assertTrue(0x0D in supported)
        assertTrue(0x11 in supported)
        assertTrue(0x1F in supported)
    }

    @Test
    fun parseSupportedPids_emptyWhenResponseMissing() {
        assertEquals(emptySet<Int>(), PidSupportBitmapParser.parseSupportedPids("NO DATA", 0x00))
    }

    @Test
    fun parseSupportedPids_secondRangeStartsAt21() {
        val supported = PidSupportBitmapParser.parseSupportedPids("41 20 80 00 00 00", 0x20)
        assertEquals(setOf(0x21), supported)
    }
}
