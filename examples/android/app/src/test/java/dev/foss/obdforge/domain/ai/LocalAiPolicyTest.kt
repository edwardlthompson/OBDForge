package dev.foss.obdforge.domain.ai

import dev.foss.obdforge.domain.livedata.PersonaMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalAiPolicyTest {
    @Test
    fun normalizeCode_acceptsStandardFormat() {
        assertEquals("P0133", LocalAiPolicy.normalizeCode(" p0133 "))
    }

    @Test
    fun normalizeCode_rejectsInvalid() {
        assertNull(LocalAiPolicy.normalizeCode("BAD"))
    }

    @Test
    fun buildLlmPrompt_includesOfflineInstruction() {
        val prompt = LocalAiPolicy.buildLlmPrompt(
            code = "P0133",
            persona = PersonaMode.Diy,
            catalogHint = DtcCatalog.lookup("P0133"),
            classification = null,
        )
        assertTrue(prompt.contains("Do not use the internet"))
        assertTrue(prompt.contains("P0133"))
    }
}
