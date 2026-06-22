package dev.foss.obdforge.data.diagnostics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class VehicleHealthScanUseCaseTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun scan_readsDemoDtcsAndAbnormalPids() = runTest {
        val registry = TransportRegistry.default(context)
        val useCase = VehicleHealthScanUseCase(registry, ProtocolRegistry.default())
        val selection = TransportSelection(
            type = TransportType.Simulated,
            endpoint = TransportEndpoint.Simulated,
        )

        val result = useCase.scan(selection, PersonaMode.Diy)

        assertTrue(result.isSuccess)
        val snapshot = result.getOrThrow()
        assertEquals(listOf("P0133", "P0171"), snapshot.dtcs)
        assertTrue(snapshot.abnormalPids.any { it.pid == 0x05 })
    }
}
