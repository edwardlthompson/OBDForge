package dev.foss.obdforge.domain.livedata

import dev.foss.obdforge.domain.protocol.ObdMode
import dev.foss.obdforge.domain.protocol.PidResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PidValueParserTest {
    @Test
    fun parse_rpmFromMode01Payload() {
        val response = PidResponse(
            mode = ObdMode.Mode01,
            pid = 0x0C,
            payload = byteArrayOf(0x0F.toByte(), 0xA0.toByte()),
            raw = "41 0C 0F A0",
        )
        val parsed = PidValueParser.parse(response)
        assertNotNull(parsed)
        assertEquals(1000.0, parsed!!.numericValue!!, 0.1)
    }

    @Test
    fun parse_speedFromSingleByte() {
        val response = PidResponse(
            mode = ObdMode.Mode01,
            pid = 0x0D,
            payload = byteArrayOf(0x32),
            raw = "41 0D 32",
        )
        val parsed = PidValueParser.parse(response)
        assertEquals(50.0, parsed?.numericValue)
    }
}
