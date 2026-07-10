package dev.foss.obdforge.domain.safety

import dev.foss.obdforge.domain.flash.FlashTransportPolicy

object SafetyGate {
    private const val STATIONARY_SPEED_KPH = 1.0

    fun evaluate(context: SafetyContext): SafetyGateResult {
        if (!PersonaPolicy.allows(context.persona, context.operation)) {
            return SafetyGateResult.Blocked(SafetyBlockReason.PersonaNotPermitted)
        }
        if (!context.adapterConnected) {
            return SafetyGateResult.Blocked(SafetyBlockReason.AdapterNotConnected)
        }
        if (!context.protocolProbed) {
            return SafetyGateResult.Blocked(SafetyBlockReason.ProtocolNotProbed)
        }
        if (context.demoMode && !context.demoStationaryAttested) {
            return SafetyGateResult.Blocked(SafetyBlockReason.DemoAttestationRequired)
        }
        if (!isVehicleStationary(context)) {
            return SafetyGateResult.Blocked(SafetyBlockReason.VehicleNotStationary)
        }
        if (PersonaPolicy.requiresExpertUnlock(context.operation) && !isExpertActive(context)) {
            return SafetyGateResult.Blocked(
                if (context.expertUnlocked) SafetyBlockReason.ExpertModeExpired
                else SafetyBlockReason.ExpertModeRequired,
            )
        }
        if (!context.userConfirmed) {
            return SafetyGateResult.Blocked(SafetyBlockReason.ConfirmationRequired)
        }
        if (context.writesThisSession >= context.maxWritesPerSession) {
            return SafetyGateResult.Blocked(SafetyBlockReason.RateLimitExceeded)
        }
        if (context.operation == WriteOperation.EcuFlash) {
            if (!FlashTransportPolicy.allows(context.transportType)) {
                return SafetyGateResult.Blocked(SafetyBlockReason.FlashTransportNotAllowed)
            }
            if (!context.brickRiskAttested) {
                return SafetyGateResult.Blocked(SafetyBlockReason.BrickRiskAttestationRequired)
            }
            if (!context.demoMode) {
                val volts = context.batteryVoltageVolts
                if (volts == null || volts < SafetyContext.FLASH_MIN_BATTERY_VOLTS) {
                    return SafetyGateResult.Blocked(SafetyBlockReason.BatteryVoltageTooLow)
                }
            }
        }
        return SafetyGateResult.Allowed
    }

    fun hashCommand(command: String): String =
        command.toByteArray().fold(0x811c9dc5.toInt()) { hash, byte ->
            (hash xor byte.toInt()) * 0x01000193
        }.toUInt().toString(16)

    private fun isExpertActive(context: SafetyContext): Boolean {
        if (!context.expertUnlocked) return false
        val expiresAt = context.expertUnlockExpiresAtMs ?: return false
        return context.nowMs < expiresAt
    }

    private fun isVehicleStationary(context: SafetyContext): Boolean {
        if (context.demoMode) return context.demoStationaryAttested
        val speed = context.vehicleSpeedKph ?: return false
        return speed <= STATIONARY_SPEED_KPH
    }
}
