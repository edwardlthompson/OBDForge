package dev.foss.obdforge.data.registry

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.domain.protocol.ProbeResult
import dev.foss.obdforge.domain.protocol.ProtocolId
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ProtocolRegistryTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun selectBest_returnsStubElm327ForSimulatedTransport() = runTest {
        val registry = ProtocolRegistry.default()
        val transport = TransportRegistry.default(context).create(
            TransportType.Simulated,
            TransportEndpoint.Simulated,
        )!!
        transport.connect()
        val protocol = registry.selectBest(transport)
        assertEquals(ProtocolId.Elm327, protocol?.id)
        assertEquals(ProbeResult.Supported, protocol?.probe(transport))
    }
}
