package dev.foss.obdforge.domain.bidirectional

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EcuCodingCommandsTest {
    @Test
    fun buildReadAndWrite() {
        val read = EcuCodingCommands.buildRead("F190").getOrThrow()
        assertEquals("22F190", read.wireCommand)
        val write = EcuCodingCommands.buildWrite("F190", "AABB").getOrThrow()
        assertEquals("2EF190AABB", write.wireCommand)
    }

    @Test
    fun rejectsFlashAndSecurityAccess() {
        assertTrue(EcuCodingCommands.rejectIfFlashShaped("34 00 00").isFailure)
        assertTrue(EcuCodingCommands.rejectIfFlashShaped("36 AA").isFailure)
        assertTrue(EcuCodingCommands.rejectIfFlashShaped("37").isFailure)
        assertTrue(EcuCodingCommands.rejectIfFlashShaped("27 01").isFailure)
        assertTrue(EcuCodingCommands.rejectIfFlashShaped("22 F1 90").isSuccess)
    }

    @Test
    fun detectsNegativeResponse() {
        assertTrue(EcuCodingCommands.isNegativeResponse("7F 22 33"))
        assertFalse(EcuCodingCommands.isNegativeResponse("62 F1 90 AA"))
    }

    @Test
    fun rejectsOversizedWrite() {
        val huge = "AA".repeat(EcuCodingCommands.MAX_DID_DATA_BYTES + 1)
        assertTrue(EcuCodingCommands.buildWrite("F190", huge).isFailure)
    }
}
