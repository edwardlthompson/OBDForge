package dev.foss.obdforge.data.demo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.data.vin.ResolveVinOutcome
import dev.foss.obdforge.data.vin.ResolveVinUseCase
import dev.foss.obdforge.data.vin.VinProfileRepository
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VinSourceType
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
class DemoVinResolutionFlowTest {
    private lateinit var database: ObdForgeDatabase
    private lateinit var useCase: ResolveVinUseCase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        useCase = ResolveVinUseCase(VinProfileRepository(database))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun resolveFromSimulatedEcu_persistsProfileWithoutHardware() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val transport = requireNotNull(
            TransportRegistry.default(context).create(
                TransportType.Simulated,
                TransportEndpoint.Simulated,
            ),
        )
        val outcome = useCase.fromEcu(transport)
        assertTrue(outcome is ResolveVinOutcome.Saved)
        val saved = outcome as ResolveVinOutcome.Saved
        assertEquals(DemoObdFixtures.DEMO_VIN, saved.profile.vin)
        assertEquals(VinSourceType.EcuObd2, saved.profile.source)
    }
}
