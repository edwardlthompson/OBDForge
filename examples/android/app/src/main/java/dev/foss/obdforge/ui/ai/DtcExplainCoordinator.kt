package dev.foss.obdforge.ui.ai

import dev.foss.obdforge.data.ai.ExplainDtcUseCase
import dev.foss.obdforge.data.diagnostics.VehicleHealthScanUseCase
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.vin.VinProfileRepository
import dev.foss.obdforge.domain.ai.DtcExplanation
import dev.foss.obdforge.domain.ai.ExplainDtcOutcome
import dev.foss.obdforge.domain.diagnostics.VehicleHealthSnapshot
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.vehicle.VinManufacturerGuesser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DtcExplainCoordinator(
    private val explainDtcUseCase: ExplainDtcUseCase,
    private val vehicleHealthScanUseCase: VehicleHealthScanUseCase,
    private val vinProfileRepository: VinProfileRepository,
    private val transportSelection: TransportSelection,
) {
    private val _snapshot = MutableStateFlow<VehicleHealthSnapshot?>(null)
    val snapshot: StateFlow<VehicleHealthSnapshot?> = _snapshot.asStateFlow()

    private val _explanations = MutableStateFlow<Map<String, DtcExplanation>>(emptyMap())
    val explanations: StateFlow<Map<String, DtcExplanation>> = _explanations.asStateFlow()

    private val _selectedCode = MutableStateFlow<String?>(null)
    val selectedCode: StateFlow<String?> = _selectedCode.asStateFlow()

    private val _explanation = MutableStateFlow<DtcExplanation?>(null)
    val explanation: StateFlow<DtcExplanation?> = _explanation.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _scanning = MutableStateFlow(false)
    val scanning: StateFlow<Boolean> = _scanning.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    suspend fun scanVehicle(persona: PersonaMode) {
        _scanning.value = true
        _statusMessage.value = null
        val manufacturer = VinManufacturerGuesser.resolve(vinProfileRepository.latest())
        vehicleHealthScanUseCase.scan(transportSelection, persona).fold(
            onSuccess = { health ->
                _snapshot.value = health
                val explained = linkedMapOf<String, DtcExplanation>()
                for (code in health.dtcs) {
                    when (val outcome = explainDtcUseCase.explain(code, persona, manufacturer)) {
                        is ExplainDtcOutcome.Success -> explained[code] = outcome.explanation
                        else -> Unit
                    }
                }
                _explanations.value = explained
                val firstCode = health.dtcs.firstOrNull()
                _selectedCode.value = firstCode
                _explanation.value = firstCode?.let(explained::get)
            },
            onFailure = {
                _statusMessage.value = STATUS_SCAN_FAILED
            },
        )
        _scanning.value = false
    }

    fun selectCode(code: String) {
        _selectedCode.value = code
        _explanation.value = _explanations.value[code]
        _statusMessage.value = null
    }

    suspend fun explainManual(code: String, persona: PersonaMode) {
        _loading.value = true
        _statusMessage.value = null
        val manufacturer = VinManufacturerGuesser.resolve(vinProfileRepository.latest())
        when (val outcome = explainDtcUseCase.explain(code, persona, manufacturer)) {
            is ExplainDtcOutcome.Success -> {
                _selectedCode.value = outcome.explanation.code
                _explanation.value = outcome.explanation
                _explanations.value = _explanations.value + (outcome.explanation.code to outcome.explanation)
            }
            ExplainDtcOutcome.InvalidCode ->
                _statusMessage.value = STATUS_INVALID_CODE
            ExplainDtcOutcome.Unavailable ->
                _statusMessage.value = STATUS_UNAVAILABLE
        }
        _loading.value = false
    }

    fun clear() {
        _snapshot.value = null
        _explanations.value = emptyMap()
        _selectedCode.value = null
        _explanation.value = null
        _statusMessage.value = null
    }

    companion object {
        const val STATUS_INVALID_CODE = "ai_error_invalid_dtc"
        const val STATUS_UNAVAILABLE = "ai_error_unavailable"
        const val STATUS_SCAN_FAILED = "ai_error_scan_failed"
    }
}
