package dev.foss.obdforge.domain.safety

import dev.foss.obdforge.domain.livedata.PersonaMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SafetyGateTest {
    @Test
    fun diyClearDtcAllowedWhenStationaryAndConfirmed() {
        val result = SafetyGate.evaluate(baseContext().copy(operation = WriteOperation.ClearDtc))
        assertTrue(result is SafetyGateResult.Allowed)
    }

    @Test
    fun diyUdsWriteBlockedByPersona() {
        val result = SafetyGate.evaluate(
            baseContext().copy(persona = PersonaMode.Diy, operation = WriteOperation.UdsWrite),
        )
        assertEquals(SafetyBlockReason.PersonaNotPermitted, (result as SafetyGateResult.Blocked).reason)
    }

    @Test
    fun racingUdsWriteRequiresExpertUnlock() {
        val result = SafetyGate.evaluate(
            baseContext().copy(
                persona = PersonaMode.Racing,
                operation = WriteOperation.UdsWrite,
                expertUnlocked = false,
            ),
        )
        assertEquals(SafetyBlockReason.ExpertModeRequired, (result as SafetyGateResult.Blocked).reason)
    }

    @Test
    fun racingUdsWriteAllowedWithExpertUnlock() {
        val now = 1_000L
        val result = SafetyGate.evaluate(
            baseContext().copy(
                persona = PersonaMode.Racing,
                operation = WriteOperation.UdsWrite,
                expertUnlocked = true,
                expertUnlockExpiresAtMs = now + ExpertUnlockPolicy.UNLOCK_DURATION_MS,
                nowMs = now,
            ),
        )
        assertTrue(result is SafetyGateResult.Allowed)
    }

    @Test
    fun expiredExpertUnlockBlocksWrite() {
        val now = 10_000L
        val result = SafetyGate.evaluate(
            baseContext().copy(
                persona = PersonaMode.Racing,
                operation = WriteOperation.ActuatorTest,
                expertUnlocked = true,
                expertUnlockExpiresAtMs = now - 1,
                nowMs = now,
            ),
        )
        assertEquals(SafetyBlockReason.ExpertModeExpired, (result as SafetyGateResult.Blocked).reason)
    }

    @Test
    fun movingVehicleBlocksWrite() {
        val result = SafetyGate.evaluate(
            baseContext().copy(vehicleSpeedKph = 25.0, demoMode = false),
        )
        assertEquals(SafetyBlockReason.VehicleNotStationary, (result as SafetyGateResult.Blocked).reason)
    }

    @Test
    fun demoModeRequiresStationaryAttestation() {
        val result = SafetyGate.evaluate(
            baseContext().copy(demoMode = true, demoStationaryAttested = false),
        )
        assertEquals(SafetyBlockReason.DemoAttestationRequired, (result as SafetyGateResult.Blocked).reason)
    }

    @Test
    fun unconfirmedWriteBlocked() {
        val result = SafetyGate.evaluate(baseContext().copy(userConfirmed = false))
        assertEquals(SafetyBlockReason.ConfirmationRequired, (result as SafetyGateResult.Blocked).reason)
    }

    @Test
    fun rateLimitBlocksRepeatedWrites() {
        val result = SafetyGate.evaluate(
            baseContext().copy(writesThisSession = SafetyContext.DEFAULT_MAX_WRITES),
        )
        assertEquals(SafetyBlockReason.RateLimitExceeded, (result as SafetyGateResult.Blocked).reason)
    }

    @Test
    fun hashCommandIsDeterministic() {
        assertEquals(SafetyGate.hashCommand("04"), SafetyGate.hashCommand("04"))
        assertTrue(SafetyGate.hashCommand("04").isNotEmpty())
    }

    private fun baseContext() = SafetyContext(
        persona = PersonaMode.Diy,
        operation = WriteOperation.ClearDtc,
        expertUnlocked = false,
        expertUnlockExpiresAtMs = null,
        nowMs = 1_000L,
        vehicleSpeedKph = 0.0,
        demoMode = true,
        demoStationaryAttested = true,
        protocolProbed = true,
        adapterConnected = true,
        userConfirmed = true,
        writesThisSession = 0,
    )
}
