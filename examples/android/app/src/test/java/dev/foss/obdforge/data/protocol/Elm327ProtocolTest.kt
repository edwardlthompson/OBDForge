package dev.foss.obdforge.data.protocol

import dev.foss.obdforge.data.demo.SimulatedObdTransport
import dev.foss.obdforge.domain.protocol.ObdMode
import dev.foss.obdforge.domain.protocol.ProbeResult
import dev.foss.obdforge.data.protocol.test.TranscriptLoader
import dev.foss.obdforge.data.protocol.test.TranscriptReplayTransport
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Elm327ProtocolTest {
    private val protocol = Elm327Protocol()

    @Test
    fun probe_transcriptFixture() = runTest {
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/elm327/probe_success.txt"),
        )
        transport.connect()
        assertEquals(ProbeResult.Supported, protocol.probe(transport))
    }

    @Test
    fun readPid_mode01_transcriptFixture() = runTest {
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/elm327/mode01_rpm.txt"),
        )
        transport.connect()
        protocol.probe(transport)
        val pid = protocol.readPid(transport, ObdMode.Mode01, 0x0C).getOrThrow()
        assertEquals(0x0C, pid.pid)
        assertEquals(2, pid.payload.size)
    }

    @Test
    fun readDtcs_transcriptFixture() = runTest {
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/elm327/mode03_dtcs.txt"),
        )
        transport.connect()
        protocol.probe(transport)
        val dtcs = protocol.readDtcs(transport).getOrThrow()
        assertEquals("P0133", dtcs.entries.first().code)
    }

    @Test
    fun clearDtcs_transcriptFixture() = runTest {
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/elm327/mode04_clear.txt"),
        )
        transport.connect()
        protocol.probe(transport)
        assertTrue(protocol.clearDtcs(transport).isSuccess)
    }

    @Test
    fun readPid_mode09_transcriptFixture() = runTest {
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/elm327/mode09_vin.txt"),
        )
        transport.connect()
        protocol.probe(transport)
        val vin = protocol.readPid(transport, ObdMode.Mode09, 0x02).getOrThrow()
        assertEquals("1G1JC5444R7251234", String(vin.payload))
    }

    @Test
    fun probe_simulatedTransport() = runTest {
        val transport = SimulatedObdTransport()
        transport.connect()
        assertEquals(ProbeResult.Supported, protocol.probe(transport))
        val rpm = protocol.readPid(transport, ObdMode.Mode01, 0x0C).getOrThrow()
        assertEquals(0x0C, rpm.pid)
        val dtcs = protocol.readDtcs(transport).getOrThrow()
        assertEquals(1, dtcs.entries.size)
        assertTrue(protocol.clearDtcs(transport).isSuccess)
    }
}
