package dev.foss.obdforge.domain.safety

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpertUnlockPolicyTest {
    @Test
    fun defaultPinIsValid() {
        assertTrue(ExpertUnlockPolicy.isValidPin(ExpertUnlockPolicy.DEFAULT_PIN))
        assertFalse(ExpertUnlockPolicy.isValidPin("0000"))
    }

    @Test
    fun expiresAtAddsDuration() {
        val unlockedAt = 1_000L
        assertEquals(
            unlockedAt + ExpertUnlockPolicy.UNLOCK_DURATION_MS,
            ExpertUnlockPolicy.expiresAt(unlockedAt),
        )
    }
}
