package dev.foss.obdforge.ui.coding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.obdforge.R
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.domain.bidirectional.BidirectionalOutcome
import dev.foss.obdforge.domain.bidirectional.BidirectionalSession
import dev.foss.obdforge.domain.livedata.PersonaMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun EcuCodingHost(
    root: ObdForgeCompositionRoot,
    scope: CoroutineScope,
    persona: PersonaMode,
    demoModeEnabled: Boolean,
    transportSelection: TransportSelection,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val unlockExpiresAtMs by root.expertUnlockPreferences.unlockExpiresAtMs
        .collectAsStateWithLifecycle(initialValue = null)
    var statusMessage by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    fun sessionNow(): BidirectionalSession {
        val now = System.currentTimeMillis()
        val expires = unlockExpiresAtMs
        val unlocked = expires != null && now < expires
        return BidirectionalSession(
            persona = persona,
            expertUnlocked = unlocked,
            expertUnlockExpiresAtMs = expires,
            demoMode = demoModeEnabled,
            nowMs = now,
        )
    }

    EcuCodingScreen(
        statusMessage = statusMessage,
        busy = busy,
        onRead = { did ->
            busy = true
            statusMessage = ""
            scope.launch {
                val outcome = root.gatedBidirectionalService.ecuCodingRead(
                    session = sessionNow(),
                    selection = transportSelection,
                    didHex = did,
                )
                statusMessage = outcomeMessage(context.getString(R.string.ecu_coding_nack_hint), outcome)
                busy = false
            }
        },
        onWrite = { did, data ->
            busy = true
            statusMessage = ""
            scope.launch {
                val outcome = root.gatedBidirectionalService.ecuCodingWrite(
                    session = sessionNow(),
                    selection = transportSelection,
                    didHex = did,
                    dataHex = data,
                )
                statusMessage = outcomeMessage(context.getString(R.string.ecu_coding_nack_hint), outcome)
                busy = false
            }
        },
        onBack = onBack,
        modifier = modifier,
    )
}

private fun outcomeMessage(nackHint: String, outcome: BidirectionalOutcome): String =
    when (outcome) {
        is BidirectionalOutcome.Executed -> {
            if (outcome.response.startsWith("NACK")) {
                "$nackHint ${outcome.response}"
            } else {
                outcome.response
            }
        }
        is BidirectionalOutcome.Blocked -> outcome.reason.name
        is BidirectionalOutcome.Failed -> outcome.message
    }
