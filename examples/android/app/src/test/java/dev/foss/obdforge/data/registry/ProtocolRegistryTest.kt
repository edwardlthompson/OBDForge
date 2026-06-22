package dev.foss.obdforge.data.registry

import dev.foss.obdforge.domain.protocol.ProbeResult
import dev.foss.obdforge.domain.protocol.ProtocolId
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ProtocolRegistryTest {
    @Test
    fun selectBest_returnsStubElm327ForSimulatedTransport() = runTest {
        val registry = ProtocolRegistry.default()
        val transport = TransportRegistry.default().create(
            dev.foss.obdforge.domain.transport.TransportType.Simulated,
        )!!
        transport.connect()
        val protocol = registry.selectBest(transport)
        assertEquals(ProtocolId.Elm327, protocol?.id)
        assertEquals(ProbeResult.Supported, protocol?.probe(transport))
    }
}
