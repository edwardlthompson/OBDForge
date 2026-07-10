package dev.foss.obdforge.data.flash

import dev.foss.obdforge.data.persistence.SafetyGateUseCase
import dev.foss.obdforge.domain.bidirectional.BidirectionalOutcome
import dev.foss.obdforge.domain.flash.DemoEcuFlashProfile
import dev.foss.obdforge.domain.flash.EcuFlashProfile
import dev.foss.obdforge.domain.flash.FlashSession
import dev.foss.obdforge.domain.flash.FlashTransferEngine
import dev.foss.obdforge.domain.safety.SafetyGateResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Gated Stage A flash entry: USB-C host (UsbSerial) or Simulated only; demo transfer plan.
 * Does not unlock coding-path rejection of raw 34/36/37.
 */
class GatedFlashService(
    private val safetyGateUseCase: SafetyGateUseCase,
    private val engine: FlashTransferEngine = FlashTransferEngine(),
) {
    private val mutex = Mutex()
    private var writesThisSession: Int = 0

    fun resetSession() {
        writesThisSession = 0
    }

    suspend fun planAndExecuteDemo(
        session: FlashSession,
        binary: ByteArray,
        profile: EcuFlashProfile = DemoEcuFlashProfile.profile,
    ): BidirectionalOutcome = mutex.withLock {
        if (!profile.isValid() || binary.isEmpty()) {
            return BidirectionalOutcome.Failed("Incomplete profile or empty binary")
        }
        val commandSummary = "FLASH ${profile.id} precheck"
        when (
            val gate = safetyGateUseCase.evaluateAndLog(
                context = session.toSafetyContext(writesThisSession),
                protocolId = "flash-demo",
                command = commandSummary,
            )
        ) {
            is SafetyGateResult.Blocked -> return BidirectionalOutcome.Blocked(gate.reason)
            is SafetyGateResult.Allowed -> Unit
        }
        val plan = engine.plan(profile, binary, allowDemoSecurity = session.demoMode).getOrElse {
            return BidirectionalOutcome.Failed(it.message ?: "Flash plan failed")
        }
        val executedSummary = "FLASH ${profile.id} sha256=${plan.fileSha256Hex} blocks=${plan.totalBlocks}"
        val responses = engine.executeDemo(plan).getOrElse {
            return BidirectionalOutcome.Failed(it.message ?: "Demo flash failed")
        }
        writesThisSession++
        BidirectionalOutcome.Executed(
            command = executedSummary,
            response = responses.joinToString(" "),
            protocolId = "flash-demo",
        )
    }
}
