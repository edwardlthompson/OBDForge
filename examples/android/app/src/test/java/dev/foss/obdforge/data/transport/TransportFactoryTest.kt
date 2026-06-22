package dev.foss.obdforge.data.transport

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TransportFactoryTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun create_simulatedTransport() {
        val transport = TransportFactory.create(
            context,
            TransportType.Simulated,
            TransportEndpoint.Simulated,
        )
        assertNotNull(transport)
        assertEquals(TransportType.Simulated, transport!!.type)
    }

    @Test
    fun create_tcpTransport() {
        val transport = TransportFactory.create(
            context,
            TransportType.WiFi,
            TransportEndpoint.Tcp("127.0.0.1", 35000),
        )
        assertNotNull(transport)
        assertEquals(TransportType.WiFi, transport!!.type)
    }
}
