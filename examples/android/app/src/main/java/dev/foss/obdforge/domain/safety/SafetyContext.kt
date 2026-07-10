package dev.foss.obdforge.domain.safety

import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.transport.TransportType

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
    val transportType: TransportType? = null,
    val brickRiskAttested: Boolean = false,
    /** Control-module / adapter voltage (V). Required for EcuFlash when not in demo. */
    val batteryVoltageVolts: Double? = null,
) {
    companion object {
        const val DEFAULT_MAX_WRITES = 5
        /** Minimum vehicle/adapter voltage before programming (12V system). */
        const val FLASH_MIN_BATTERY_VOLTS = 12.0
    }
}
