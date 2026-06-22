package dev.foss.obdforge.ui.connect

import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.vehicle.VinSourceType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ConnectDemoCoordinatorTest {
    @Test
    fun connectAndReadVin_returnsEcuOrDemo() = runTest {
        val coordinator = ConnectDemoCoordinator(
            transportRegistry = TransportRegistry.default(),
            protocolRegistry = ProtocolRegistry.default(),
        )
        val result = coordinator.connectAndReadVin()
        assertEquals(17, result.vin.length)
        assertEquals(VinSourceType.EcuObd2, result.source)
        coordinator.disconnect()
    }
}
