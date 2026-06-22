package dev.foss.obdforge.data.transport

import org.junit.Assert.assertEquals
import org.junit.Test

class ObdLineCodecTest {
    @Test
    fun drainLines_splitsOnCarriageReturn() {
        val buffer = StringBuilder()
        val lines = ObdLineCodec.drainLines(buffer, "41 0C 0F A0\r> ")
        assertEquals(listOf("41 0C 0F A0"), lines)
        assertEquals("> ", buffer.toString())
    }

    @Test
    fun encode_appendsCarriageReturn() {
        assertEquals("ATZ\r", String(ObdLineCodec.encode("ATZ")))
    }
}
