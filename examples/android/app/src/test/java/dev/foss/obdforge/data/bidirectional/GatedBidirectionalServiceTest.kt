package dev.foss.obdforge.data.bidirectional

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.demo.DemoDiagnosticFlow
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.persistence.AuditLogRepository
import dev.foss.obdforge.data.persistence.SafetyGateUseCase
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.bidirectional.BidirectionalOutcome
import dev.foss.obdforge.domain.bidirectional.BidirectionalSession
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.safety.ExpertUnlockPolicy
import dev.foss.obdforge.domain.safety.SafetyBlockReason
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
class GatedBidirectionalServiceTest {
    private lateinit var context: Context
    private lateinit var service: GatedBidirectionalService
    private lateinit var auditLogRepository: AuditLogRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        auditLogRepository = AuditLogRepository(database)
        service = GatedBidirectionalService(
            executor = ObdBidirectionalExecutor(
                TransportRegistry.default(context),
                ProtocolRegistry.default(),
            ),
            safetyGateUseCase = SafetyGateUseCase(auditLogRepository),
        )
    }

    @After
    fun tearDown() {
        // in-memory db closes with process
    }

    @Test
    fun diyClearDtcAllowedInDemo() = runTest {
        val outcome = service.clearDtcs(diySession(), DemoDiagnosticFlow.demoSelection)
        assertTrue(outcome is BidirectionalOutcome.Executed)
    }

    @Test
    fun diyUdsWriteBlockedByPersonaPolicy() = runTest {
        val outcome = service.udsWrite(
            session = diySession(),
            selection = DemoDiagnosticFlow.demoSelection,
            command = "2F 01 00",
        )
        assertEquals(SafetyBlockReason.PersonaNotPermitted, (outcome as BidirectionalOutcome.Blocked).reason)
        assertEquals(1, auditLogRepository.count())
        assertTrue(auditLogRepository.allRecords().first().outcome.contains("PersonaNotPermitted"))
    }

    @Test
    fun diyActuatorTestBlockedByPersonaPolicy() = runTest {
        val outcome = service.runActuatorTest(
            session = diySession(),
            selection = DemoDiagnosticFlow.demoSelection,
            testIdHex = "01",
        )
        assertEquals(SafetyBlockReason.PersonaNotPermitted, (outcome as BidirectionalOutcome.Blocked).reason)
    }

    @Test
    fun racingActuatorTestRequiresExpertUnlock() = runTest {
        val outcome = service.runActuatorTest(
            session = racingSession(expertUnlocked = false),
            selection = DemoDiagnosticFlow.demoSelection,
            testIdHex = "01",
        )
        assertEquals(SafetyBlockReason.ExpertModeRequired, (outcome as BidirectionalOutcome.Blocked).reason)
    }

    @Test
    fun racingExpertUdsWriteSucceedsInDemo() = runTest {
        val now = 1_000L
        val outcome = service.udsWrite(
            session = racingSession(expertUnlocked = true, nowMs = now),
            selection = DemoDiagnosticFlow.demoSelection,
            command = "2F 01 00",
        )
        assertTrue(outcome is BidirectionalOutcome.Executed)
        assertEquals("6F 01 00", (outcome as BidirectionalOutcome.Executed).response)
    }

    private fun diySession() = BidirectionalSession(
        persona = PersonaMode.Diy,
        expertUnlocked = false,
        expertUnlockExpiresAtMs = null,
        demoMode = true,
    )

    private fun racingSession(expertUnlocked: Boolean, nowMs: Long = 1_000L) = BidirectionalSession(
        persona = PersonaMode.Racing,
        expertUnlocked = expertUnlocked,
        expertUnlockExpiresAtMs = if (expertUnlocked) ExpertUnlockPolicy.expiresAt(nowMs) else null,
        demoMode = true,
        nowMs = nowMs,
    )
}
