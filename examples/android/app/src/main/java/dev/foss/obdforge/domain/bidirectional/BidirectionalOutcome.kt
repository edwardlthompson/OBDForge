package dev.foss.obdforge.domain.bidirectional

import dev.foss.obdforge.domain.safety.SafetyBlockReason

sealed class BidirectionalOutcome {
    data class Executed(
        val command: String,
        val response: String,
        val protocolId: String,
    ) : BidirectionalOutcome()

    data class Blocked(val reason: SafetyBlockReason) : BidirectionalOutcome()

    data class Failed(val message: String) : BidirectionalOutcome()
}
