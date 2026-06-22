package dev.foss.obdforge.domain.safety

import dev.foss.obdforge.domain.livedata.PersonaMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonaPolicyTest {
    @Test
    fun diy_allowsClearDtcOnly() {
        assertTrue(PersonaPolicy.allows(PersonaMode.Diy, WriteOperation.ClearDtc))
        assertFalse(PersonaPolicy.allows(PersonaMode.Diy, WriteOperation.UdsWrite))
    }

    @Test
    fun semiPro_allowsLimitedBidirectional() {
        assertTrue(PersonaPolicy.allows(PersonaMode.SemiPro, WriteOperation.ClearDtc))
        assertTrue(PersonaPolicy.allows(PersonaMode.SemiPro, WriteOperation.ActuatorTest))
        assertFalse(PersonaPolicy.allows(PersonaMode.SemiPro, WriteOperation.UdsWrite))
    }

    @Test
    fun shopAndRacing_allowAllWrites() {
        for (operation in WriteOperation.entries) {
            assertTrue(PersonaPolicy.allows(PersonaMode.Shop, operation))
            assertTrue(PersonaPolicy.allows(PersonaMode.Racing, operation))
        }
    }
}
