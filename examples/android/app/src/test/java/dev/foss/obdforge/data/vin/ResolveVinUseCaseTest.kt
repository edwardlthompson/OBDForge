package dev.foss.obdforge.data.vin

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.demo.DemoObdFixtures
import dev.foss.obdforge.data.demo.SimulatedObdTransport
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.domain.vehicle.VinResolver
import dev.foss.obdforge.domain.vehicle.VinSourceType
import dev.foss.obdforge.domain.vehicle.VinValidationError
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ResolveVinUseCaseTest {
    private lateinit var database: ObdForgeDatabase
    private lateinit var repository: VinProfileRepository
    private lateinit var useCase: ResolveVinUseCase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        repository = VinProfileRepository(database)
        useCase = ResolveVinUseCase(repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun fromEcu_savesValidatedProfile() = runTest {
        val outcome = useCase.fromEcu(SimulatedObdTransport())
        assertTrue(outcome is ResolveVinOutcome.Saved)
        val saved = outcome as ResolveVinOutcome.Saved
        assertEquals(DemoObdFixtures.DEMO_VIN, saved.profile.vin)
        assertEquals(VinSourceType.EcuObd2, saved.profile.source)
    }

    @Test
    fun fromManual_rejectsInvalidCheckDigit() = runTest {
        val outcome = useCase.fromManual("1G1JC5444R7251234")
        assertTrue(outcome is ResolveVinOutcome.ValidationFailed)
        assertEquals(
            VinValidationError.CheckDigitMismatch,
            (outcome as ResolveVinOutcome.ValidationFailed).error,
        )
    }

    @Test
    fun fromManual_savesValidVin() = runTest {
        val outcome = useCase.fromManual(VinResolver.DEMO_VIN)
        assertTrue(outcome is ResolveVinOutcome.Saved)
        assertEquals(VinResolver.DEMO_VIN, (outcome as ResolveVinOutcome.Saved).profile.vin)
        assertEquals(VinSourceType.Manual, outcome.profile.source)
    }
}
