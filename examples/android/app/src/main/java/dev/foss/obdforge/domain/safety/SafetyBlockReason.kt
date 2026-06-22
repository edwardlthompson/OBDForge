package dev.foss.obdforge.domain.safety

enum class SafetyBlockReason {
    PersonaNotPermitted,
    ExpertModeRequired,
    ExpertModeExpired,
    VehicleNotStationary,
    ProtocolNotProbed,
    AdapterNotConnected,
    ConfirmationRequired,
    RateLimitExceeded,
    DemoAttestationRequired,
}
