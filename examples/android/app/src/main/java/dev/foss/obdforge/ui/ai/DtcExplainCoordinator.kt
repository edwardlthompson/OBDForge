package dev.foss.obdforge.ui.ai

import dev.foss.obdforge.data.ai.ExplainDtcUseCase
import dev.foss.obdforge.domain.ai.DtcExplanation
import dev.foss.obdforge.domain.ai.ExplainDtcOutcome
import dev.foss.obdforge.domain.livedata.PersonaMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DtcExplainCoordinator(
    private val explainDtcUseCase: ExplainDtcUseCase,
) {
    private val _explanation = MutableStateFlow<DtcExplanation?>(null)
    val explanation: StateFlow<DtcExplanation?> = _explanation.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    suspend fun explain(code: String, persona: PersonaMode) {
        _loading.value = true
        _statusMessage.value = null
        when (val outcome = explainDtcUseCase.explain(code, persona)) {
            is ExplainDtcOutcome.Success -> {
                _explanation.value = outcome.explanation
            }
            ExplainDtcOutcome.InvalidCode ->
                _statusMessage.value = STATUS_INVALID_CODE
            ExplainDtcOutcome.Unavailable ->
                _statusMessage.value = STATUS_UNAVAILABLE
        }
        _loading.value = false
    }

    fun clear() {
        _explanation.value = null
        _statusMessage.value = null
    }

    companion object {
        const val STATUS_INVALID_CODE = "ai_error_invalid_dtc"
        const val STATUS_UNAVAILABLE = "ai_error_unavailable"
    }
}
