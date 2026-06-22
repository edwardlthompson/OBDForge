package dev.foss.obdforge.domain.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DtcCatalogTest {
    @Test
    fun explain_demoPrimaryDtc() {
        val explanation = DtcCatalog.explain("P0133")
        assertNotNull(explanation)
        assertEquals("P0133", explanation!!.code)
        assertEquals(AiExplanationSource.Catalog, explanation.source)
    }

    @Test
    fun explain_unknownCodeReturnsNull() {
        assertNull(DtcCatalog.explain("P9999"))
    }
}
