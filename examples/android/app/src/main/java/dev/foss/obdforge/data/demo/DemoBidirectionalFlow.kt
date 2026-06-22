package dev.foss.obdforge.data.demo

import dev.foss.obdforge.data.bidirectional.GatedBidirectionalService
import dev.foss.obdforge.data.bidirectional.ObdBidirectionalExecutor
import dev.foss.obdforge.data.persistence.AuditLogRepository
import dev.foss.obdforge.data.persistence.SafetyGateUseCase
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.bidirectional.BidirectionalOutcome
import dev.foss.obdforge.domain.bidirectional.BidirectionalSession
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.safety.ExpertUnlockPolicy

object DemoBidirectionalFlow {
    private const val DEMO_ACTUATOR_TEST_ID = "01"
    private const val DEMO_UDS_COMMAND = "2F 01 00"

    suspend fun runRacingExpertFlow(
        transportRegistry: TransportRegistry,
        protocolRegistry: ProtocolRegistry,
        auditLogRepository: AuditLogRepository,
        nowMs: Long = System.currentTimeMillis(),
    ): Result<DemoBidirectionalFlowResult> {
        val service = GatedBidirectionalService(
            executor = ObdBidirectionalExecutor(transportRegistry, protocolRegistry),
            safetyGateUseCase = SafetyGateUseCase(auditLogRepository),
        )
        val session = BidirectionalSession(
            persona = PersonaMode.Racing,
            expertUnlocked = true,
            expertUnlockExpiresAtMs = ExpertUnlockPolicy.expiresAt(nowMs),
            demoMode = true,
            nowMs = nowMs,
        )
        val selection = DemoDiagnosticFlow.demoSelection

        val clearOutcome = service.clearDtcs(session, selection)
        if (clearOutcome !is BidirectionalOutcome.Executed) {
            return Result.failure(IllegalStateException("Clear DTC failed: $clearOutcome"))
        }
        val actuatorOutcome = service.runActuatorTest(session, selection, DEMO_ACTUATOR_TEST_ID)
        if (actuatorOutcome !is BidirectionalOutcome.Executed) {
            return Result.failure(IllegalStateException("Actuator test failed: $actuatorOutcome"))
        }
        val udsOutcome = service.udsWrite(session, selection, DEMO_UDS_COMMAND)
        if (udsOutcome !is BidirectionalOutcome.Executed) {
            return Result.failure(IllegalStateException("UDS write failed: $udsOutcome"))
        }

        return Result.success(
            DemoBidirectionalFlowResult(
                clearOutcome = clearOutcome,
                actuatorOutcome = actuatorOutcome,
                udsOutcome = udsOutcome,
                auditEntryCount = auditLogRepository.count(),
            ),
        )
    }

    suspend fun attemptDiyUdsWrite(
        transportRegistry: TransportRegistry,
        protocolRegistry: ProtocolRegistry,
        auditLogRepository: AuditLogRepository,
    ): BidirectionalOutcome {
        val service = GatedBidirectionalService(
            executor = ObdBidirectionalExecutor(transportRegistry, protocolRegistry),
            safetyGateUseCase = SafetyGateUseCase(auditLogRepository),
        )
        return service.udsWrite(
            session = BidirectionalSession(
                persona = PersonaMode.Diy,
                expertUnlocked = false,
                expertUnlockExpiresAtMs = null,
                demoMode = true,
            ),
            selection = DemoDiagnosticFlow.demoSelection,
            command = DEMO_UDS_COMMAND,
        )
    }
}

data class DemoBidirectionalFlowResult(
    val clearOutcome: BidirectionalOutcome.Executed,
    val actuatorOutcome: BidirectionalOutcome.Executed,
    val udsOutcome: BidirectionalOutcome.Executed,
    val auditEntryCount: Int,
)
