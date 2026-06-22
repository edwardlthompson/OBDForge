package dev.foss.obdforge.data.demo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.persistence.AuditLogRepository
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.bidirectional.BidirectionalOutcome
import dev.foss.obdforge.domain.safety.SafetyBlockReason
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DemoBidirectionalFlowTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun racingExpertFlowRunsClearActuatorAndUdsWithoutHardware() = runTest {
        val database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        val auditLogRepository = AuditLogRepository(database)
        val result = DemoBidirectionalFlow.runRacingExpertFlow(
            transportRegistry = TransportRegistry.default(context),
            protocolRegistry = ProtocolRegistry.default(),
            auditLogRepository = auditLogRepository,
            nowMs = 5_000L,
        ).getOrThrow()

        assertEquals("44", result.clearOutcome.response)
        assertEquals("48 01 00", result.actuatorOutcome.response)
        assertEquals("6F 01 00", result.udsOutcome.response)
        assertEquals(3, result.auditEntryCount)
        database.close()
    }

    @Test
    fun diyUdsWriteBlockedAndAudited() = runTest {
        val database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        val auditLogRepository = AuditLogRepository(database)
        val outcome = DemoBidirectionalFlow.attemptDiyUdsWrite(
            transportRegistry = TransportRegistry.default(context),
            protocolRegistry = ProtocolRegistry.default(),
            auditLogRepository = auditLogRepository,
        )
        assertEquals(SafetyBlockReason.PersonaNotPermitted, (outcome as BidirectionalOutcome.Blocked).reason)
        assertEquals(1, auditLogRepository.count())
        database.close()
    }
}
