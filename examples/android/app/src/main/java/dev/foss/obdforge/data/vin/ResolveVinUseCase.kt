package dev.foss.obdforge.data.vin

import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.vehicle.VinReadResult
import dev.foss.obdforge.domain.vehicle.VinResolver
import dev.foss.obdforge.domain.vehicle.VinValidationError
import dev.foss.obdforge.domain.vehicle.VinValidationResult
import dev.foss.obdforge.domain.vehicle.VinValidator
import dev.foss.obdforge.domain.vehicle.VehicleProfile

sealed class ResolveVinOutcome {
    data class Saved(val profile: VehicleProfile, val read: VinReadResult) : ResolveVinOutcome()
    data class ValidationFailed(val error: VinValidationError) : ResolveVinOutcome()
    data object EcuUnavailable : ResolveVinOutcome()
}

class ResolveVinUseCase(
    private val vinProfileRepository: VinProfileRepository,
) {
    suspend fun fromEcu(transport: ObdTransport, adapterIdHash: String? = null): ResolveVinOutcome {
        val read = VinResolver.resolveFromEcu(transport) ?: return ResolveVinOutcome.EcuUnavailable
        return saveValidated(read, adapterIdHash)
    }

    suspend fun fromManual(input: String, adapterIdHash: String? = null): ResolveVinOutcome {
        val read = VinResolver.fromManual(input)
            ?: return validationError(input)
        return saveValidated(read, adapterIdHash)
    }

    suspend fun fromBarcode(input: String, adapterIdHash: String? = null): ResolveVinOutcome {
        val read = VinResolver.fromBarcode(input)
            ?: return validationError(input)
        return saveValidated(read, adapterIdHash)
    }

    private suspend fun saveValidated(read: VinReadResult, adapterIdHash: String?): ResolveVinOutcome {
        val validated = when (val result = VinValidator.validate(read.vin)) {
            is VinValidationResult.Valid -> read.copy(vin = result.vin)
            is VinValidationResult.Invalid -> return ResolveVinOutcome.ValidationFailed(result.reason)
        }
        val profile = vinProfileRepository.save(validated, adapterIdHash)
        return ResolveVinOutcome.Saved(profile, validated)
    }

    private fun validationError(input: String): ResolveVinOutcome {
        val error = when (val result = VinValidator.validate(input)) {
            is VinValidationResult.Invalid -> result.reason
            is VinValidationResult.Valid -> VinValidationError.CheckDigitMismatch
        }
        return ResolveVinOutcome.ValidationFailed(error)
    }
}
