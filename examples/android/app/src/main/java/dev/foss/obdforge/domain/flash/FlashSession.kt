package dev.foss.obdforge.domain.flash

import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.safety.SafetyContext
import dev.foss.obdforge.domain.safety.WriteOperation
import dev.foss.obdforge.domain.transport.TransportType

data class FlashSession(
    val persona: PersonaMode,
    val expertUnlocked: Boolean,
    val expertUnlockExpiresAtMs: Long?,
    val demoMode: Boolean,
    val transportType: TransportType,
    val brickRiskAttested: Boolean,
    val demoStationaryAttested: Boolean = true,
    val vehicleSpeedKph: Double? = 0.0,
    val batteryVoltageVolts: Double? = 13.5,
    val protocolProbed: Boolean = true,
    val adapterConnected: Boolean = true,
    val userConfirmed: Boolean = true,
    val nowMs: Long = System.currentTimeMillis(),
) {
    fun toSafetyContext(writesThisSession: Int): SafetyContext =
        SafetyContext(
            persona = persona,
            operation = WriteOperation.EcuFlash,
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
            transportType = transportType,
            brickRiskAttested = brickRiskAttested,
            batteryVoltageVolts = batteryVoltageVolts,
        )
}
