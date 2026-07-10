package dev.foss.obdforge.domain.safety

enum class WriteOperation {
    ClearDtc,
    ActuatorTest,
    UdsWrite,
    EcuCoding,
    EcuFlash,
}
