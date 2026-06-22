package dev.foss.obdforge.domain.safety

sealed class SafetyGateResult {
    data object Allowed : SafetyGateResult()

    data class Blocked(val reason: SafetyBlockReason) : SafetyGateResult()
}
