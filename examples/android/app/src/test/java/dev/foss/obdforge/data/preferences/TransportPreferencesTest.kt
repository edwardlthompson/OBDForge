package dev.foss.obdforge.data.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TransportPreferencesTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun setSelection_persistsTcpEndpoint() = runTest {
        val prefs = TransportPreferences(context)
        val endpoint = TransportEndpoint.Tcp("10.0.0.5", 35000)
        prefs.setSelection(TransportType.WiFi, endpoint)
        val saved = prefs.selection.first()
        assertEquals(TransportType.WiFi, saved.type)
        assertEquals(endpoint, saved.endpoint)
    }
}
