package dev.foss.obdforge.domain.vehicle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VinValidatorTest {
    @Test
    fun validate_acceptsDemoVinWithCorrectCheckDigit() {
        val result = VinValidator.validate(VinResolver.DEMO_VIN)
        assertTrue(result is VinValidationResult.Valid)
        assertEquals(VinResolver.DEMO_VIN, (result as VinValidationResult.Valid).vin)
    }

    @Test
    fun validate_rejectsWrongLength() {
        val result = VinValidator.validate("123")
        assertTrue(result is VinValidationResult.Invalid)
        assertEquals(
            VinValidationError.WrongLength,
            (result as VinValidationResult.Invalid).reason,
        )
    }

    @Test
    fun validate_rejectsInvalidCharacters() {
        val result = VinValidator.validate("1G1JC5442R725123O")
        assertTrue(result is VinValidationResult.Invalid)
        assertEquals(
            VinValidationError.InvalidCharacters,
            (result as VinValidationResult.Invalid).reason,
        )
    }

    @Test
    fun validate_rejectsBadCheckDigit() {
        val result = VinValidator.validate("1G1JC5444R7251234")
        assertTrue(result is VinValidationResult.Invalid)
        assertEquals(
            VinValidationError.CheckDigitMismatch,
            (result as VinValidationResult.Invalid).reason,
        )
    }

    @Test
    fun normalize_stripsSeparatorsAndUppercases() {
        assertEquals(
            VinResolver.DEMO_VIN,
            VinValidator.normalize("1g1-jc5442 r7251234"),
        )
    }

    @Test
    fun computeCheckDigit_matchesPositionNine() {
        assertEquals('2', VinValidator.computeCheckDigit(VinResolver.DEMO_VIN))
    }
}
