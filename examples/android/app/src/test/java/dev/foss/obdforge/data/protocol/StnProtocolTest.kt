package dev.foss.obdforge.data.protocol

import dev.foss.obdforge.data.protocol.test.TranscriptLoader
import dev.foss.obdforge.data.protocol.test.TranscriptReplayTransport
import dev.foss.obdforge.domain.protocol.ObdMode
import dev.foss.obdforge.domain.protocol.ProbeResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class StnProtocolTest {
    private val protocol = StnProtocol()

    @Test
    fun probe_transcriptFixture() = runTest {
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/stn/probe_success.txt"),
        )
        transport.connect()
        assertEquals(ProbeResult.Supported, protocol.probe(transport))
    }

    @Test
    fun readPid_mode01_transcriptFixture() = runTest {
        val transport = TranscriptReplayTransport(
            TranscriptLoader.loadClasspath("protocol/stn/mode01_rpm.txt"),
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
            TranscriptLoader.loadClasspath("protocol/stn/mode03_dtcs.txt"),
        )
        transport.connect()
        protocol.probe(transport)
        val dtcs = protocol.readDtcs(transport).getOrThrow()
        assertEquals("P0133", dtcs.entries.first().code)
    }
}
