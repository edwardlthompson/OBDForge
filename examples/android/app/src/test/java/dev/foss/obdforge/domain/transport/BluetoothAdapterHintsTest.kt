package dev.foss.obdforge.domain.transport

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BluetoothAdapterHintsTest {
    @Test
    fun prefersClassic_forObdLinkMx() {
        assertTrue(BluetoothAdapterHints.prefersClassicSpp("OBDLink MX"))
        assertTrue(BluetoothAdapterHints.prefersClassicSpp("OBDLink MX+"))
        assertTrue(BluetoothAdapterHints.prefersClassicSpp("obdlink cx"))
        assertEquals(BluetoothLinkKind.Classic, BluetoothAdapterHints.defaultLinkKind("OBDLink MX"))
    }

    @Test
    fun auto_forGenericClone() {
        assertFalse(BluetoothAdapterHints.prefersClassicSpp("ELM327 BLE"))
        assertEquals(BluetoothLinkKind.Auto, BluetoothAdapterHints.defaultLinkKind("ELM327 BLE"))
        assertEquals(BluetoothLinkKind.Auto, BluetoothAdapterHints.defaultLinkKind(null))
    }

    @Test
    fun blankAddress() {
        assertTrue(BluetoothAdapterHints.isBlankAddress(""))
        assertTrue(BluetoothAdapterHints.isBlankAddress(null))
        assertFalse(BluetoothAdapterHints.isBlankAddress("AA:BB:CC:DD:EE:FF"))
    }
}
