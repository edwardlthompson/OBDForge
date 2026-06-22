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
        val racing = PidCatalog.forPersona(PersonaMode.Racing)
        assertTrue(racing.size > diy.size)
    }
}
