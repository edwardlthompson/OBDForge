package dev.foss.obdforge.data.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.safety.SafetyContext
import dev.foss.obdforge.domain.safety.SafetyGateResult
import dev.foss.obdforge.domain.safety.WriteOperation
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
class SafetyGateUseCaseTest {
    private lateinit var database: ObdForgeDatabase
    private lateinit var useCase: SafetyGateUseCase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        useCase = SafetyGateUseCase(AuditLogRepository(database))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun logsBlockedAttempt() = runTest {
        val result = useCase.evaluateAndLog(
            context = blockedContext(),
            protocolId = "elm327",
            command = "2F 01 00",
        )
        assertTrue(result is SafetyGateResult.Blocked)
        val records = AuditLogRepository(database).allRecords()
        assertEquals(1, records.size)
        assertTrue(records.first().outcome.startsWith("blocked:"))
    }

    @Test
    fun logsAllowedAttempt() = runTest {
        val result = useCase.evaluateAndLog(
            context = allowedContext(),
            protocolId = "elm327",
            command = "04",
        )
        assertTrue(result is SafetyGateResult.Allowed)
        val records = AuditLogRepository(database).allRecords()
        assertEquals("allowed", records.first().outcome)
    }

    private fun allowedContext() = SafetyContext(
        persona = PersonaMode.Diy,
        operation = WriteOperation.ClearDtc,
        expertUnlocked = false,
        expertUnlockExpiresAtMs = null,
        nowMs = 100L,
        vehicleSpeedKph = 0.0,
        demoMode = true,
        demoStationaryAttested = true,
        protocolProbed = true,
        adapterConnected = true,
        userConfirmed = true,
        writesThisSession = 0,
    )

    private fun blockedContext() = allowedContext().copy(operation = WriteOperation.UdsWrite)
}
