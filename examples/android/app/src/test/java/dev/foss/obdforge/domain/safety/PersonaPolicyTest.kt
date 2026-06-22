package dev.foss.obdforge.domain.safety

import dev.foss.obdforge.domain.livedata.PersonaMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonaPolicyTest {
    @Test
    fun diyAllowsClearDtcOnly() {
        assertTrue(PersonaPolicy.allows(PersonaMode.Diy, WriteOperation.ClearDtc))
        assertFalse(PersonaPolicy.allows(PersonaMode.Diy, WriteOperation.UdsWrite))
    }

    @Test
    fun racingAllowsAllWriteOperations() {
        WriteOperation.entries.forEach { operation ->
            assertTrue(PersonaPolicy.allows(PersonaMode.Racing, operation))
        }
    }

    @Test
    fun clearDtcDoesNotRequireExpertUnlock() {
        assertFalse(PersonaPolicy.requiresExpertUnlock(WriteOperation.ClearDtc))
    }

    @Test
    fun advancedWritesRequireExpertUnlock() {
        assertTrue(PersonaPolicy.requiresExpertUnlock(WriteOperation.UdsWrite))
        assertTrue(PersonaPolicy.requiresExpertUnlock(WriteOperation.ActuatorTest))
        assertTrue(PersonaPolicy.requiresExpertUnlock(WriteOperation.EcuCoding))
    }
}
