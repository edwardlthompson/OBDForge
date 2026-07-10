package dev.foss.obdforge.data.flash

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.persistence.AuditLogRepository
import dev.foss.obdforge.data.persistence.SafetyGateUseCase
import dev.foss.obdforge.domain.bidirectional.BidirectionalOutcome
import dev.foss.obdforge.domain.flash.DemoEcuFlashProfile
import dev.foss.obdforge.domain.flash.FlashSession
import dev.foss.obdforge.domain.flash.SecurityAccessRegistry
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.safety.ExpertUnlockPolicy
import dev.foss.obdforge.domain.safety.SafetyBlockReason
import dev.foss.obdforge.domain.transport.TransportType
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class GatedFlashServiceTest {
    private lateinit var service: GatedFlashService

    @Before
    fun setUp() {
        SecurityAccessRegistry.reset()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        service = GatedFlashService(SafetyGateUseCase(AuditLogRepository(database)))
    }

    @After
    fun tearDown() {
        SecurityAccessRegistry.reset()
    }

    @Test
    fun bluetoothTransport_blocked() = runTest {
        val outcome = service.planAndExecuteDemo(
            session = flashSession(TransportType.Bluetooth),
            binary = byteArrayOf(1, 2, 3),
        )
        assertEquals(
            SafetyBlockReason.FlashTransportNotAllowed,
            (outcome as BidirectionalOutcome.Blocked).reason,
        )
    }

    @Test
    fun wifiTransport_blocked() = runTest {
        val outcome = service.planAndExecuteDemo(
            session = flashSession(TransportType.WiFi),
            binary = byteArrayOf(1, 2, 3),
        )
        assertEquals(
            SafetyBlockReason.FlashTransportNotAllowed,
            (outcome as BidirectionalOutcome.Blocked).reason,
        )
    }

    @Test
    fun diy_blockedByPersona() = runTest {
        val outcome = service.planAndExecuteDemo(
            session = flashSession(TransportType.Simulated).copy(persona = PersonaMode.Diy),
            binary = byteArrayOf(1),
        )
        assertEquals(
            SafetyBlockReason.PersonaNotPermitted,
            (outcome as BidirectionalOutcome.Blocked).reason,
        )
    }

    @Test
    fun demoUsb_succeedsWithAttestation() = runTest {
        val outcome = service.planAndExecuteDemo(
            session = flashSession(TransportType.UsbSerial),
            binary = ByteArray(16) { 0x11 },
            profile = DemoEcuFlashProfile.profile,
        )
        assertTrue(outcome is BidirectionalOutcome.Executed)
    }

    @Test
    fun missingBrickAttestation_blocked() = runTest {
        val outcome = service.planAndExecuteDemo(
            session = flashSession(TransportType.Simulated).copy(brickRiskAttested = false),
            binary = byteArrayOf(1),
        )
        assertEquals(
            SafetyBlockReason.BrickRiskAttestationRequired,
            (outcome as BidirectionalOutcome.Blocked).reason,
        )
    }

    private fun flashSession(type: TransportType) = FlashSession(
        persona = PersonaMode.Racing,
        expertUnlocked = true,
        expertUnlockExpiresAtMs = ExpertUnlockPolicy.expiresAt(1_000L),
        demoMode = true,
        transportType = type,
        brickRiskAttested = true,
        nowMs = 1_000L,
    )
}
