package dev.foss.obdforge.ui.connect

import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VinSourceType
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ConnectDemoCoordinatorTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun connectAndReadVin_returnsEcuOrDemo() = runTest {
        val coordinator = ConnectDemoCoordinator(
            transportRegistry = TransportRegistry.default(context),
            protocolRegistry = ProtocolRegistry.default(),
            selection = TransportSelection(
                type = TransportType.Simulated,
                endpoint = TransportEndpoint.Simulated,
            ),
        )
        val result = coordinator.connectAndReadVin()
        assertEquals(17, result.vin.length)
        assertEquals(VinSourceType.EcuObd2, result.source)
        coordinator.disconnect()
    }
}
