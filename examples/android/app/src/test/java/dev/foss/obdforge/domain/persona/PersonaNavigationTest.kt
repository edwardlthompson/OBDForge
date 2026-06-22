package dev.foss.obdforge.domain.persona

import dev.foss.obdforge.domain.livedata.PersonaMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonaNavigationTest {
    @Test
    fun shopDestination_onlyVisibleForShopPersona() {
        assertTrue(PersonaNavigation.isVisible(PersonaMode.Shop, AppDestination.Shop))
        assertFalse(PersonaNavigation.isVisible(PersonaMode.Diy, AppDestination.Shop))
        assertFalse(PersonaNavigation.isVisible(PersonaMode.Racing, AppDestination.Shop))
    }

    @Test
    fun auditExport_visibleForShopAndRacing() {
        assertTrue(PersonaNavigation.showsAuditExport(PersonaMode.Shop))
        assertTrue(PersonaNavigation.showsAuditExport(PersonaMode.Racing))
        assertFalse(PersonaNavigation.showsAuditExport(PersonaMode.Diy))
    }

    @Test
    fun expertMode_hiddenForDiy() {
        assertFalse(PersonaNavigation.showsExpertMode(PersonaMode.Diy))
        assertTrue(PersonaNavigation.showsExpertMode(PersonaMode.SemiPro))
    }
}
