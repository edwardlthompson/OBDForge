package dev.foss.obdforge.data.bidirectional

import dev.foss.obdforge.data.persistence.SafetyGateUseCase
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.domain.bidirectional.BidirectionalOutcome
import dev.foss.obdforge.domain.bidirectional.BidirectionalSession
import dev.foss.obdforge.domain.bidirectional.EcuCodingCommands
import dev.foss.obdforge.domain.safety.SafetyGateResult
import dev.foss.obdforge.domain.safety.WriteOperation
import kotlinx.coroutines.withTimeoutOrNull

class GatedBidirectionalService(
    private val executor: ObdBidirectionalExecutor,
    private val safetyGateUseCase: SafetyGateUseCase,
) {
    private var writesThisSession: Int = 0

    fun resetWriteSession() {
        writesThisSession = 0
    }

    suspend fun clearDtcs(
        session: BidirectionalSession,
        selection: TransportSelection,
    ): BidirectionalOutcome = runGated(
        session = session,
        operation = WriteOperation.ClearDtc,
        command = "04",
        selection = selection,
    ) { active -> executor.clearDtcs(active) }

    suspend fun udsWrite(
        session: BidirectionalSession,
        selection: TransportSelection,
        command: String,
    ): BidirectionalOutcome {
        EcuCodingCommands.rejectIfFlashShaped(command).getOrElse {
            return BidirectionalOutcome.Failed(it.message ?: "Unsupported command")
        }
        return runGated(
            session = session,
            operation = WriteOperation.UdsWrite,
            command = command,
            selection = selection,
        ) { active -> executor.sendRaw(active, command) }
    }

    suspend fun runActuatorTest(
        session: BidirectionalSession,
        selection: TransportSelection,
        testIdHex: String,
    ): BidirectionalOutcome {
        val command = "08${testIdHex.trim().uppercase().padStart(2, '0')}"
        return runGated(
            session = session,
            operation = WriteOperation.ActuatorTest,
            command = command,
            selection = selection,
        ) { active -> executor.sendRaw(active, command) }
    }

    suspend fun ecuCodingRead(
        session: BidirectionalSession,
        selection: TransportSelection,
        didHex: String,
    ): BidirectionalOutcome {
        val request = EcuCodingCommands.buildRead(didHex).getOrElse {
            return BidirectionalOutcome.Failed(it.message ?: "Invalid DID")
        }
        return runCoding(session, selection, request.wireCommand)
    }

    suspend fun ecuCodingWrite(
        session: BidirectionalSession,
        selection: TransportSelection,
        didHex: String,
        dataHex: String,
    ): BidirectionalOutcome {
        val request = EcuCodingCommands.buildWrite(didHex, dataHex).getOrElse {
            return BidirectionalOutcome.Failed(it.message ?: "Invalid coding payload")
        }
        return runCoding(session, selection, request.wireCommand)
    }

    private suspend fun runCoding(
        session: BidirectionalSession,
        selection: TransportSelection,
        wireCommand: String,
    ): BidirectionalOutcome = runGated(
        session = session,
        operation = WriteOperation.EcuCoding,
        command = wireCommand,
        selection = selection,
        timeoutMs = EcuCodingCommands.REQUEST_TIMEOUT_MS,
    ) { active ->
        executor.sendRaw(active, wireCommand).map { response ->
            if (EcuCodingCommands.isNegativeResponse(response)) "NACK: $response" else response
        }
    }

    private suspend fun runGated(
        session: BidirectionalSession,
        operation: WriteOperation,
        command: String,
        selection: TransportSelection,
        timeoutMs: Long? = null,
        execute: suspend (ObdBidirectionalExecutor.ActiveSession) -> Result<String>,
    ): BidirectionalOutcome {
        val activeSession = executor.openSession(selection).getOrElse {
            return BidirectionalOutcome.Failed(it.message ?: "Connection failed")
        }
        val protocolId = activeSession.protocol.id.wireName
        return try {
            when (
                val gate = safetyGateUseCase.evaluateAndLog(
                    context = session.toSafetyContext(operation, writesThisSession),
                    protocolId = protocolId,
                    command = command,
                )
            ) {
                is SafetyGateResult.Blocked -> BidirectionalOutcome.Blocked(gate.reason)
                is SafetyGateResult.Allowed -> {
                    val responseResult = if (timeoutMs != null) {
                        withTimeoutOrNull(timeoutMs) { execute(activeSession) }
                            ?: Result.failure(IllegalStateException("Request timed out after ${timeoutMs}ms"))
                    } else {
                        execute(activeSession)
                    }
                    responseResult.fold(
                        onSuccess = { response ->
                            writesThisSession++
                            BidirectionalOutcome.Executed(
                                command = command,
                                response = response,
                                protocolId = protocolId,
                            )
                        },
                        onFailure = { error ->
                            BidirectionalOutcome.Failed(error.message ?: "Command failed")
                        },
                    )
                }
            }
        } finally {
            executor.closeSession(activeSession)
        }
    }
}
