package dev.foss.obdforge.data.livedata

import dev.foss.obdforge.data.demo.SimulatedObdTransport
import dev.foss.obdforge.data.protocol.Elm327Protocol
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LiveDataStreamEngineTest {
    @Test
    fun stream_emitsPidSamples() = runTest {
        val transport = SimulatedObdTransport()
        transport.connect()
        val protocol = Elm327Protocol()
        protocol.probe(transport)
        val engine = LiveDataStreamEngine(
            protocol = protocol,
            transport = transport,
            pids = listOf(0x0C, 0x0D),
            pollIntervalMs = 20,
        )
        engine.start(this)
        advanceTimeBy(100)
        assertTrue(engine.snapshot.value.samples.containsKey(0x0C))
        assertTrue(engine.snapshot.value.samples.containsKey(0x0D))
        engine.stop()
    }

    @Test
    fun pause_skipsPolling() = runTest {
        val transport = SimulatedObdTransport()
        transport.connect()
        val protocol = Elm327Protocol()
        protocol.probe(transport)
        val engine = LiveDataStreamEngine(
            protocol = protocol,
            transport = transport,
            pids = listOf(0x0C),
            pollIntervalMs = 20,
        )
        engine.start(this)
        advanceTimeBy(50)
        val beforePause = engine.snapshot.value.sequence
        engine.pause()
        advanceTimeBy(200)
        assertEquals(beforePause, engine.snapshot.value.sequence)
        engine.stop()
    }
}
