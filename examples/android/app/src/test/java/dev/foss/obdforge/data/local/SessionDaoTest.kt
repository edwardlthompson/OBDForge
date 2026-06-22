package dev.foss.obdforge.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.entity.AuditLogEntity
import dev.foss.obdforge.data.local.entity.SessionEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SessionDaoTest {
    private lateinit var database: ObdForgeDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertSessionAndAuditLog() = runTest {
        database.sessionDao().insert(
            SessionEntity(
                startedAtEpochMs = 1L,
                transportType = "Simulated",
                protocolId = "elm327",
                vin = "1G1JC5444R7251234",
            ),
        )
        database.auditLogDao().insert(
            AuditLogEntity(
                timestampEpochMs = 2L,
                action = "connect",
                outcome = "ok",
                detail = null,
            ),
        )
        assertEquals(1, database.sessionDao().count())
        assertEquals(1, database.auditLogDao().count())
    }
}
