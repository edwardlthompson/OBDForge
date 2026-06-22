package dev.foss.obdforge.data.persistence

import dev.foss.obdforge.domain.session.DtcSnapshotRecord
import dev.foss.obdforge.domain.session.FreezeFrameRecord
import dev.foss.obdforge.domain.session.SessionDetail
import dev.foss.obdforge.domain.session.SessionSummary
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SessionJsonExporterTest {
    @Test
    fun export_includesSessionAndDtcData() {
        val json = SessionJsonExporter.export(
            SessionDetail(
                summary = SessionSummary(
                    id = 1L,
                    startedAtEpochMs = 1000L,
                    endedAtEpochMs = 2000L,
                    transportType = "Simulated",
                    protocolId = "elm327",
                    vin = "1G1JC5442R7251234",
                    dtcCount = 1,
                ),
                dtcSnapshots = listOf(
                    DtcSnapshotRecord(
                        capturedAtEpochMs = 1500L,
                        codes = listOf("P0133"),
                        rawResponse = "43 01 33 00",
                    ),
                ),
                freezeFrames = listOf(
                    FreezeFrameRecord(
                        dtcCode = "P0133",
                        capturedAtEpochMs = 1501L,
                        pidValues = mapOf("0C" to "1000 rpm"),
                    ),
                ),
            ),
        )
        assertTrue(json.contains("\"vin\": \"1G1JC5442R7251234\""))
        assertTrue(json.contains("P0133"))
        assertTrue(json.contains("freezeFrames"))
    }
}
