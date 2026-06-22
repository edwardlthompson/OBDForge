package dev.foss.obdforge.domain.vehicle

sealed class VinValidationResult {
    data class Valid(val vin: String) : VinValidationResult()
    data class Invalid(val reason: VinValidationError) : VinValidationResult()
}

enum class VinValidationError {
    WrongLength,
    InvalidCharacters,
    CheckDigitMismatch,
}

object VinValidator {
    private val VIN_PATTERN = Regex("^[A-HJ-NPR-Z0-9]{17}$")
    private val TRANSLITERATION = mapOf(
        'A' to 1, 'B' to 2, 'C' to 3, 'D' to 4, 'E' to 5, 'F' to 6, 'G' to 7, 'H' to 8,
        'J' to 1, 'K' to 2, 'L' to 3, 'M' to 4, 'N' to 5, 'P' to 7, 'R' to 9,
        'S' to 2, 'T' to 3, 'U' to 4, 'V' to 5, 'W' to 6, 'X' to 7, 'Y' to 8, 'Z' to 9,
    )
    private val WEIGHTS = intArrayOf(8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2)

    fun normalize(input: String): String =
        input.uppercase().replace(Regex("[^A-Z0-9]"), "")

    fun validate(input: String): VinValidationResult {
        val vin = normalize(input)
        if (vin.length != 17) return VinValidationResult.Invalid(VinValidationError.WrongLength)
        if (!VIN_PATTERN.matches(vin)) return VinValidationResult.Invalid(VinValidationError.InvalidCharacters)
        val expected = computeCheckDigit(vin)
        if (vin[8] != expected) return VinValidationResult.Invalid(VinValidationError.CheckDigitMismatch)
        return VinValidationResult.Valid(vin)
    }

    fun computeCheckDigit(vin: String): Char {
        require(vin.length == 17)
        var sum = 0
        for (i in vin.indices) {
            val value = vin[i].digitToIntOrNull() ?: TRANSLITERATION.getValue(vin[i])
            sum += value * WEIGHTS[i]
        }
        val remainder = sum % 11
        return if (remainder == 10) 'X' else ('0' + remainder)
    }
}
