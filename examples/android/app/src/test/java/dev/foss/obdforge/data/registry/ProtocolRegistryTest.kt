package dev.foss.obdforge.data.registry

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.protocol.test.TranscriptLoader
import dev.foss.obdforge.data.protocol.test.TranscriptReplayTransport
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
    fun selectBest_returnsElm327ForSimulatedTransport() = runTest {
        val registry = ProtocolRegistry.default()
        val transport = TransportRegistry.default(context).create(
            TransportType.Simulated,
            TransportEndpoint.Simulated,
        )!!
        transport.connect()
        val protocol = registry.selectBest(transport)
        assertEquals(ProtocolId.Elm327, protocol?.id)
    }

    @Test
    fun selectBest_prefersStpxForCapableAdapter() = runTest {
        val registry = ProtocolRegistry.default()
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/stpx/probe_success.txt"),
        )
        transport.connect()
        assertEquals(ProtocolId.Stpx, registry.selectBest(transport)?.id)
    }

    @Test
    fun selectBest_fallsBackToStnForLegacyObdLink() = runTest {
        val registry = ProtocolRegistry.default()
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/stn/probe_success.txt"),
        )
        transport.connect()
        assertEquals(ProtocolId.Stn, registry.selectBest(transport)?.id)
    }

    @Test
    fun selectBest_fallsBackToElm327WhenStnProbeFails() = runTest {
        val registry = ProtocolRegistry.default()
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/elm327/probe_success.txt"),
        )
        transport.connect()
        assertEquals(ProtocolId.Elm327, registry.selectBest(transport)?.id)
    }
}
