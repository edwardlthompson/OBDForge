package dev.foss.obdforge.data.vin

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.domain.vehicle.VinReadResult
import dev.foss.obdforge.domain.vehicle.VinResolver
import dev.foss.obdforge.domain.vehicle.VinSourceType
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class VinProfileRepositoryTest {
    private lateinit var database: ObdForgeDatabase
    private lateinit var repository: VinProfileRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        repository = VinProfileRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun save_prefersEcuSourceOverManualOnConflict() = runTest {
        repository.save(
            VinReadResult(VinResolver.DEMO_VIN, VinSourceType.Manual, 0.75f),
        )
        val updated = repository.save(
            VinReadResult(VinResolver.DEMO_VIN, VinSourceType.EcuObd2, 0.95f),
        )
        assertEquals(VinSourceType.EcuObd2, updated.source)
        assertEquals(VinSourceType.EcuObd2, repository.latest()!!.source)
    }

    @Test
    fun save_keepsEcuWhenManualSavedLater() = runTest {
        repository.save(
            VinReadResult(VinResolver.DEMO_VIN, VinSourceType.EcuObd2, 0.95f),
        )
        val updated = repository.save(
            VinReadResult(VinResolver.DEMO_VIN, VinSourceType.Manual, 0.75f),
        )
        assertEquals(VinSourceType.EcuObd2, updated.source)
    }
}
