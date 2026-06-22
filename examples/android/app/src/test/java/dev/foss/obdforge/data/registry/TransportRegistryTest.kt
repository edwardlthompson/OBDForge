package dev.foss.obdforge.data.registry

import dev.foss.obdforge.domain.transport.TransportType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TransportRegistryTest {
    @Test
    fun default_registersSimulatedTransport() {
        val registry = TransportRegistry.default()
        assertTrue(TransportType.Simulated in registry.availableTypes())
        val transport = registry.create(TransportType.Simulated)
        assertNotNull(transport)
        assertEquals(TransportType.Simulated, transport!!.type)
    }
}
