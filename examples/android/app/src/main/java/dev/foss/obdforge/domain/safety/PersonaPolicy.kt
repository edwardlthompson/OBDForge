package dev.foss.obdforge.domain.safety

import dev.foss.obdforge.domain.livedata.PersonaMode

object PersonaPolicy {
    fun allows(persona: PersonaMode, operation: WriteOperation): Boolean = when (persona) {
        PersonaMode.Diy -> operation == WriteOperation.ClearDtc
        PersonaMode.SemiPro -> operation == WriteOperation.ClearDtc ||
            operation == WriteOperation.ActuatorTest
        PersonaMode.Shop, PersonaMode.Racing -> true
    }

    fun requiresExpertUnlock(operation: WriteOperation): Boolean = when (operation) {
        WriteOperation.ClearDtc -> false
        WriteOperation.ActuatorTest,
        WriteOperation.UdsWrite,
        WriteOperation.EcuCoding,
        -> true
    }
}
