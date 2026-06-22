package dev.foss.obdforge.domain.safety

import dev.foss.obdforge.domain.livedata.PersonaMode

data class SafetyContext(
    val persona: PersonaMode,
    val operation: WriteOperation,
    val expertUnlocked: Boolean,
    val expertUnlockExpiresAtMs: Long?,
    val nowMs: Long,
    val vehicleSpeedKph: Double?,
    val demoMode: Boolean,
    val demoStationaryAttested: Boolean,
    val protocolProbed: Boolean,
    val adapterConnected: Boolean,
    val userConfirmed: Boolean,
    val writesThisSession: Int,
    val maxWritesPerSession: Int = DEFAULT_MAX_WRITES,
) {
    companion object {
        const val DEFAULT_MAX_WRITES = 5
    }
}
