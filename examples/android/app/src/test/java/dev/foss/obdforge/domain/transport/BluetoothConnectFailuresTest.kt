package dev.foss.obdforge.domain.transport

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.util.concurrent.TimeoutException

class BluetoothConnectFailuresTest {
    @Test
    fun classifiesTypedException() {
        val error = BluetoothConnectException(
            BluetoothConnectFailure.BusyOrRefused,
            "busy",
        )
        assertEquals(BluetoothConnectFailure.BusyOrRefused, BluetoothConnectFailures.classify(error))
    }

    @Test
    fun classifiesMessageHeuristics() {
        assertEquals(
            BluetoothConnectFailure.NotBonded,
            BluetoothConnectFailures.classify(IllegalStateException("Adapter not bonded")),
        )
        assertEquals(
            BluetoothConnectFailure.BusyOrRefused,
            BluetoothConnectFailures.classify(IOException("Connection refused")),
        )
        assertEquals(
            BluetoothConnectFailure.Timeout,
            BluetoothConnectFailures.classify(TimeoutException("timed out")),
        )
        assertEquals(
            BluetoothConnectFailure.BleProfileMissing,
            BluetoothConnectFailures.classify(IllegalStateException("No OBD BLE profile found")),
        )
        assertEquals(
            BluetoothConnectFailure.PermissionDenied,
            BluetoothConnectFailures.classify(SecurityException("permission")),
        )
    }
}
