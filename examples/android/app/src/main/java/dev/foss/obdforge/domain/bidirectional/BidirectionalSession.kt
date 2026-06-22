package dev.foss.obdforge.domain.bidirectional

import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.safety.SafetyContext
import dev.foss.obdforge.domain.safety.WriteOperation

data class BidirectionalSession(
    val persona: PersonaMode,
    val expertUnlocked: Boolean,
    val expertUnlockExpiresAtMs: Long?,
    val demoMode: Boolean,
    val demoStationaryAttested: Boolean = true,
    val vehicleSpeedKph: Double? = if (demoMode) 0.0 else 0.0,
    val protocolProbed: Boolean = true,
    val adapterConnected: Boolean = true,
    val userConfirmed: Boolean = true,
    val nowMs: Long = System.currentTimeMillis(),
) {
    fun toSafetyContext(operation: WriteOperation, writesThisSession: Int): SafetyContext =
        SafetyContext(
            persona = persona,
            operation = operation,
            expertUnlocked = expertUnlocked,
            expertUnlockExpiresAtMs = expertUnlockExpiresAtMs,
            nowMs = nowMs,
            vehicleSpeedKph = vehicleSpeedKph,
            demoMode = demoMode,
            demoStationaryAttested = demoStationaryAttested,
            protocolProbed = protocolProbed,
            adapterConnected = adapterConnected,
            userConfirmed = userConfirmed,
            writesThisSession = writesThisSession,
        )
}
