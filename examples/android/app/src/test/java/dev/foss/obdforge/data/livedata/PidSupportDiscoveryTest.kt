package dev.foss.obdforge.data.livedata

import dev.foss.obdforge.data.demo.SimulatedObdTransport
import dev.foss.obdforge.data.protocol.Elm327Protocol
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PidSupportDiscoveryTest {
    @Test
    fun discoverSupportedCatalogPids_filtersToVehicleBitmap() = runBlocking {
        val transport = SimulatedObdTransport()
        transport.connect().getOrThrow()
        val protocol = Elm327Protocol()
        val supported = PidSupportDiscovery().discoverSupportedCatalogPids(
            protocol = protocol,
            transport = transport,
            catalogPids = listOf(0x0C, 0x0D, 0x2F, 0x42),
        )
        assertTrue(0x0C in supported)
        assertTrue(0x0D in supported)
        assertFalse(0x2F in supported)
        assertFalse(0x42 in supported)
    }
}
