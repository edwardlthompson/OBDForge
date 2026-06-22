package dev.foss.obdforge.ui.connect

import dev.foss.obdforge.data.transport.buildTransportEndpoint
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BuildEndpointTest {
    @Test
    fun buildEndpoint_tcp() {
        val endpoint = buildTransportEndpoint(
            type = TransportType.WiFi,
            tcpHost = "192.168.0.10",
            tcpPort = "35000",
            bluetoothAddress = "",
            bluetoothName = null,
            usbDeviceName = "",
        )
        assertEquals(TransportEndpoint.Tcp("192.168.0.10", 35000), endpoint)
    }

    @Test
    fun buildEndpoint_invalidTcpPort() {
        val endpoint = buildTransportEndpoint(
            type = TransportType.WiFi,
            tcpHost = "192.168.0.10",
            tcpPort = "bad",
            bluetoothAddress = "",
            bluetoothName = null,
            usbDeviceName = "",
        )
        assertNull(endpoint)
    }
}
