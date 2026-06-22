package dev.foss.obdforge.data.diagnostics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DiagnosticLogSanitizerTest {
    @Test
    fun redactsVinAndMac() {
        val input = "Adapter 1A:2B:3C:4D:5E:6F for VIN 1HGCM82633A004352 failed"
        val sanitized = DiagnosticLogSanitizer.sanitize(input)
        assertFalse(sanitized.contains("1HGCM82633A004352"))
        assertFalse(sanitized.contains("1A:2B:3C:4D:5E:6F"))
        assertEquals("Adapter [MAC] for VIN [VIN] failed", sanitized)
    }
}
