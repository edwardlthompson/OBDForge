package dev.foss.obdforge.domain.protocol

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StnResponseParserTest {
    @Test
    fun looksLikeStn_matchesObdLinkId() {
        assertTrue(StnResponseParser.looksLikeStn("STN2232 v5.10.3"))
        assertFalse(StnResponseParser.looksLikeStn("ELM327 v1.5"))
    }

    @Test
    fun parseCapabilities_detectsStpxOnNewerChips() {
        val caps = StnResponseParser.parseCapabilities(
            stiResponse = "STN2232 v5.10.3",
            stixResponse = "STN2232 v5.10.3 [2024.02.01]",
        )
        assertEquals("2232", caps?.chipId)
        assertTrue(caps?.supportsStpx == true)
    }

    @Test
    fun parseCapabilities_legacyChipDoesNotSupportStpx() {
        val caps = StnResponseParser.parseCapabilities(
            stiResponse = "STN1170 v4.20.1",
            stixResponse = "STN1170 v4.20.1 [2020.01.01]",
        )
        assertEquals("1170", caps?.chipId)
        assertFalse(caps?.supportsStpx == true)
    }
}
