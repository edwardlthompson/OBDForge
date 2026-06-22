package dev.foss.obdforge.data.protocol

import dev.foss.obdforge.data.protocol.test.TranscriptLoader
import dev.foss.obdforge.data.protocol.test.TranscriptReplayTransport
import dev.foss.obdforge.domain.protocol.FastStreamingCapable
import dev.foss.obdforge.domain.protocol.ObdMode
import dev.foss.obdforge.domain.protocol.ProbeResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StpxProtocolTest {
    private val protocol = StpxProtocol()

    @Test
    fun probe_transcriptFixture() = runTest {
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/stpx/probe_success.txt"),
        )
        transport.connect()
        assertEquals(ProbeResult.Supported, protocol.probe(transport))
    }

    @Test
    fun readPid_mode01_usesStpxCommand() = runTest {
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/stpx/mode01_rpm_stpx.txt"),
        )
        transport.connect()
        protocol.probe(transport)
        val pid = protocol.readPid(transport, ObdMode.Mode01, 0x0C).getOrThrow()
        assertEquals(0x0C, pid.pid)
        assertTrue(transport.commands.any { it.startsWith("STPX d:010C") })
    }

    @Test
    fun readPidsBatched_transcriptFixture() = runTest {
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/stpx/batch_rpm_speed.txt"),
        )
        transport.connect()
        protocol.probe(transport)
        val batch = (protocol as FastStreamingCapable).readPidsBatched(
            transport,
            ObdMode.Mode01,
            listOf(0x0C, 0x0D),
        ).getOrThrow()
        assertEquals(2, batch.size)
        assertEquals(0x0C, batch[0].pid)
        assertEquals(0x0D, batch[1].pid)
        assertTrue(transport.commands.contains("010C|010D"))
    }
}
