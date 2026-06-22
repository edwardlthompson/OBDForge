package dev.foss.obdforge.domain.protocol

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObdIsoResponseParserTest {
    @Test
    fun parseMode01_rpmPid() {
        val parsed = ObdIsoResponseParser.parseMode01("41 0C 0F A0", 0x0C)
        requireNotNull(parsed)
        assertEquals(0x0C, parsed.pid)
        assertEquals(2, parsed.payload.size)
        assertEquals(0x0F.toByte(), parsed.payload[0])
        assertEquals(0xA0.toByte(), parsed.payload[1])
    }

    @Test
    fun parseMode03_singleDtc() {
        val parsed = requireNotNull(ObdIsoResponseParser.parseMode03("43 01 33 00 00 00 00 00"))
        assertEquals(1, parsed.entries.size)
        assertEquals("P0133", parsed.entries.first().code)
    }

    @Test
    fun parseMode04_success() {
        assertTrue(ObdIsoResponseParser.parseMode04("44"))
    }

    @Test
    fun parseMode09Vin() {
        val vin = ObdIsoResponseParser.parseMode09Vin(
            "49 02 01 31 47 31 4A 43 35 34 34 34 52 37 32 35 31 32 33 34",
        )
        assertEquals("1G1JC5444R7251234", vin)
    }

    @Test
    fun decodeDtc_formatsPCode() {
        assertEquals("P0133", ObdIsoResponseParser.decodeDtc(0x01, 0x33))
    }
}
