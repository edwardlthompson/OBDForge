package dev.foss.obdforge.data.persistence

import dev.foss.obdforge.domain.safety.SafetyContext
import dev.foss.obdforge.domain.safety.SafetyGate
import dev.foss.obdforge.domain.safety.SafetyGateResult

class SafetyGateUseCase(
    private val auditLogRepository: AuditLogRepository,
) {
    suspend fun evaluateAndLog(
        context: SafetyContext,
        protocolId: String?,
        command: String,
        userNote: String? = null,
    ): SafetyGateResult {
        val result = SafetyGate.evaluate(context)
        auditLogRepository.recordAttempt(
            persona = context.persona.name,
            protocolId = protocolId,
            commandType = context.operation.name,
            commandHash = SafetyGate.hashCommand(command),
            outcome = when (result) {
                is SafetyGateResult.Allowed -> "allowed"
                is SafetyGateResult.Blocked -> "blocked:${result.reason.name}"
            },
            userNote = userNote,
            timestampEpochMs = context.nowMs,
        )
        return result
    }
}
