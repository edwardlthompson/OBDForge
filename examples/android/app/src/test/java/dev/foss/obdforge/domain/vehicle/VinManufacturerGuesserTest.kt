package dev.foss.obdforge.domain.vehicle

import dev.foss.obdforge.data.demo.DemoObdFixtures
import org.junit.Assert.assertEquals
import org.junit.Test

class VinManufacturerGuesserTest {
    @Test
    fun guessFromVin_demoVinMapsToChevy() {
        assertEquals("CHEVY", VinManufacturerGuesser.guessFromVin(DemoObdFixtures.DEMO_VIN))
    }

    @Test
    fun resolve_usesLabelBeforeVin() {
        val profile = VehicleProfile(
            vin = DemoObdFixtures.DEMO_VIN,
            source = VinSourceType.Demo,
            resolvedAtEpochMs = 0L,
            adapterIdHash = null,
            label = "Ford",
        )
        assertEquals("FORD", VinManufacturerGuesser.resolve(profile))
    }
}
