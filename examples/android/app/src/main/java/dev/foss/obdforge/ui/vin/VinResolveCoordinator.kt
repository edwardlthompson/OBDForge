package dev.foss.obdforge.ui.vin

import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.data.vin.ResolveVinOutcome
import dev.foss.obdforge.data.vin.ResolveVinUseCase
import dev.foss.obdforge.data.vin.VinProfileRepository
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VehicleProfile
import dev.foss.obdforge.domain.vehicle.VinValidationError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VinResolveCoordinator(
    private val resolveVinUseCase: ResolveVinUseCase,
    private val vinProfileRepository: VinProfileRepository,
    private val transportRegistry: TransportRegistry,
    private val transportSelection: TransportSelection,
    private val demoModeEnabled: Boolean,
) {
    private val _latestProfile = MutableStateFlow<VehicleProfile?>(null)
    val latestProfile: StateFlow<VehicleProfile?> = _latestProfile.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _showCamera = MutableStateFlow(false)
    val showCamera: StateFlow<Boolean> = _showCamera.asStateFlow()

    suspend fun refreshProfile() {
        _latestProfile.value = vinProfileRepository.latest()
    }

    val ecuResolveEnabled: Boolean
        get() = demoModeEnabled || transportSelection.type != TransportType.Simulated

    suspend fun resolveFromEcu() {
        val transport = transportRegistry.create(transportSelection.type, transportSelection.endpoint)
        if (transport == null) {
            _statusMessage.value = STATUS_TRANSPORT_UNAVAILABLE
            return
        }
        when (val outcome = resolveVinUseCase.fromEcu(transport)) {
            is ResolveVinOutcome.Saved -> {
                _latestProfile.value = outcome.profile
                _statusMessage.value = null
            }
            ResolveVinOutcome.EcuUnavailable ->
                _statusMessage.value = STATUS_ECU_UNAVAILABLE
            is ResolveVinOutcome.ValidationFailed ->
                _statusMessage.value = validationKey(outcome.error)
        }
    }

    suspend fun saveManual(vin: String) {
        applyOutcome(resolveVinUseCase.fromManual(vin))
    }

    suspend fun saveBarcode(vin: String) {
        _showCamera.value = false
        applyOutcome(resolveVinUseCase.fromBarcode(vin))
    }

    fun openScanner() {
        _showCamera.value = true
        _statusMessage.value = null
    }

    fun closeScanner() {
        _showCamera.value = false
    }

    private suspend fun applyOutcome(outcome: ResolveVinOutcome) {
        when (outcome) {
            is ResolveVinOutcome.Saved -> {
                _latestProfile.value = outcome.profile
                _statusMessage.value = null
            }
            is ResolveVinOutcome.ValidationFailed ->
                _statusMessage.value = validationKey(outcome.error)
            ResolveVinOutcome.EcuUnavailable ->
                _statusMessage.value = STATUS_ECU_UNAVAILABLE
        }
    }

    private fun validationKey(error: VinValidationError): String = when (error) {
        VinValidationError.WrongLength -> STATUS_ERROR_LENGTH
        VinValidationError.InvalidCharacters -> STATUS_ERROR_CHARSET
        VinValidationError.CheckDigitMismatch -> STATUS_ERROR_CHECK_DIGIT
    }

    companion object {
        const val STATUS_ERROR_LENGTH = "vin_error_length"
        const val STATUS_ERROR_CHARSET = "vin_error_charset"
        const val STATUS_ERROR_CHECK_DIGIT = "vin_error_check_digit"
        const val STATUS_ECU_UNAVAILABLE = "vin_ecu_unavailable"
        const val STATUS_TRANSPORT_UNAVAILABLE = "vin_transport_unavailable"
    }
}
