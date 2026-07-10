package dev.foss.obdforge.domain.safety

import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.transport.TransportType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SafetyGateFlashTest {
    @Test
    fun ecuFlash_blocksBluetooth() {
        val result = SafetyGate.evaluate(
            base().copy(
                operation = WriteOperation.EcuFlash,
                transportType = TransportType.Bluetooth,
                brickRiskAttested = true,
            ),
        )
        assertEquals(
            SafetyBlockReason.FlashTransportNotAllowed,
            (result as SafetyGateResult.Blocked).reason,
        )
    }

    @Test
    fun ecuFlash_allowsUsbWithAttestation() {
        val result = SafetyGate.evaluate(
            base().copy(
                operation = WriteOperation.EcuFlash,
                transportType = TransportType.UsbSerial,
                brickRiskAttested = true,
            ),
        )
        assertTrue(result is SafetyGateResult.Allowed)
    }

    @Test
    fun ecuFlash_blocksWifi() {
        val result = SafetyGate.evaluate(
            base().copy(
                operation = WriteOperation.EcuFlash,
                transportType = TransportType.WiFi,
                brickRiskAttested = true,
            ),
        )
        assertEquals(
            SafetyBlockReason.FlashTransportNotAllowed,
            (result as SafetyGateResult.Blocked).reason,
        )
    }

    @Test
    fun ecuFlash_blocksLowBatteryWhenNotDemo() {
        val result = SafetyGate.evaluate(
            base().copy(
                operation = WriteOperation.EcuFlash,
                transportType = TransportType.UsbSerial,
                brickRiskAttested = true,
                demoMode = false,
                batteryVoltageVolts = 11.5,
            ),
        )
        assertEquals(
            SafetyBlockReason.BatteryVoltageTooLow,
            (result as SafetyGateResult.Blocked).reason,
        )
    }

    private fun base() = SafetyContext(
        persona = PersonaMode.Shop,
        operation = WriteOperation.EcuFlash,
        expertUnlocked = true,
        expertUnlockExpiresAtMs = ExpertUnlockPolicy.expiresAt(1_000L),
        nowMs = 1_000L,
        vehicleSpeedKph = 0.0,
        demoMode = false,
        demoStationaryAttested = true,
        protocolProbed = true,
        adapterConnected = true,
        userConfirmed = true,
        writesThisSession = 0,
        batteryVoltageVolts = 13.5,
    )
}
