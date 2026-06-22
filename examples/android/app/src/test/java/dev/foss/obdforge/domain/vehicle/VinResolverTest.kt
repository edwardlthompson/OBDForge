package dev.foss.obdforge.domain.vehicle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class VinResolverTest {
    @Test
    fun parseMode09Vin_extracts17Chars() {
        val response = "49 02 01 31 47 31 4A 43 35 34 34 34 52 37 32 35 31 32 33 34"
        val vin = VinResolver.parseMode09Vin(response)
        assertNotNull(vin)
        assertEquals(17, vin!!.length)
        assertEquals("1G1JC5444R7251234", vin)
    }

    @Test
    fun demoVin_isValidLength() {
        assertEquals(17, VinResolver.demoVin().vin.length)
    }
}
