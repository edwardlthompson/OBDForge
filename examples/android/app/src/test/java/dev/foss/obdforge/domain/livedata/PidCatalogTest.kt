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
    fun forPersona_racingIncludesMoreThanDiy() {
        val diy = PidCatalog.forPersona(PersonaMode.Diy)
        val semiPro = PidCatalog.forPersona(PersonaMode.SemiPro)
        val racing = PidCatalog.forPersona(PersonaMode.Racing)
        assertTrue(semiPro.size > diy.size)
        assertTrue(racing.size >= semiPro.size)
    }
}
