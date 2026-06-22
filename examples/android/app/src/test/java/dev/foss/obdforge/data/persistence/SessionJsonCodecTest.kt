package dev.foss.obdforge.data.persistence

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SessionJsonCodecTest {
    @Test
    fun roundTripCodesAndPidValues() {
        val codes = listOf("P0133", "P0171")
        val encoded = SessionJsonCodec.encodeCodes(codes)
        assertEquals(codes, SessionJsonCodec.decodeCodes(encoded))

        val pidValues = mapOf("0C" to "1000 rpm", "0D" to "50 km/h")
        val pidJson = SessionJsonCodec.encodePidValues(pidValues)
        assertEquals(pidValues, SessionJsonCodec.decodePidValues(pidJson))
    }
}
