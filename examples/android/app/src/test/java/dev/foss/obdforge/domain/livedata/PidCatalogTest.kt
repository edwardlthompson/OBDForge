package dev.foss.obdforge.domain.livedata

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PidCatalogTest {
    @Test
    fun catalog_containsStandardPids() {
        assertEquals("Engine RPM", PidCatalog.get(0x0C)?.name)
        assertEquals(PidUnit.Kph, PidCatalog.get(0x0D)?.unit)
    }

    @Test
    fun catalog_includesFuelLoopStatus() {
        assertEquals("Fuel loop", PidCatalog.get(0x03)?.name)
        assertEquals(PidUnit.None, PidCatalog.get(0x03)?.unit)
        assertTrue(PidCatalog.forPersona(PersonaMode.Diy).any { it.pid == 0x03 })
    }

    @Test
    fun catalog_includesNarrowAndWidebandO2() {
        assertEquals(PidUnit.Volts, PidCatalog.get(0x14)?.unit)
        assertEquals(PidUnit.Lambda, PidCatalog.get(0x24)?.unit)
        assertEquals(PidUnit.Lambda, PidCatalog.get(0x44)?.unit)
    }

    @Test
    fun forPersona_racingIncludesMoreThanDiy() {
        val diy = PidCatalog.forPersona(PersonaMode.Diy)
        val semiPro = PidCatalog.forPersona(PersonaMode.SemiPro)
        val racing = PidCatalog.forPersona(PersonaMode.Racing)
        assertTrue(semiPro.size > diy.size)
        assertTrue(racing.size >= semiPro.size)
        assertTrue(diy.any { it.pid == 0x14 })
        assertTrue(diy.any { it.pid == 0x24 })
    }
}
