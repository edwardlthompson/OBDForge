package dev.foss.obdforge.domain.protocol

import org.junit.Assert.assertEquals
import org.junit.Test

class BatchedObdResponseParserTest {
    @Test
    fun parseMode01Batch_splitsPipeSeparatedResponses() {
        val parsed = BatchedObdResponseParser.parseMode01Batch(
            response = "41 0C 0F A0 | 41 0D 65",
            pids = listOf(0x0C, 0x0D),
        )
        assertEquals(2, parsed.size)
        assertEquals(0x0C, parsed[0].pid)
        assertEquals(0x0D, parsed[1].pid)
    }
}
