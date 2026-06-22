package dev.foss.obdforge.domain.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DtcManufacturerNormalizerTest {
    @Test
    fun normalize_mapsChevroletToChevy() {
        assertEquals("CHEVY", DtcManufacturerNormalizer.normalize("Chevrolet"))
    }

    @Test
    fun lookupKeys_expandsGmFamily() {
        val keys = DtcManufacturerNormalizer.lookupKeys("GM")
        assertTrue("CHEVY" in keys)
        assertTrue("GMC" in keys)
    }
}
