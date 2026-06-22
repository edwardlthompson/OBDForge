package dev.foss.obdforge.data.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.domain.protocol.DtcEntry
import dev.foss.obdforge.domain.protocol.DtcList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SessionRepositoryTest {
    private lateinit var database: ObdForgeDatabase
    private lateinit var repository: SessionRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        repository = SessionRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveAndExportSession() = runTest {
        val sessionId = repository.startSession("Simulated", "elm327", "TESTVIN1234567890")
        repository.saveDtcSnapshot(
            sessionId,
            DtcList(listOf(DtcEntry("P0133", "01 33")), "43 01 33 00"),
        )
        repository.saveFreezeFrame(sessionId, "P0133", mapOf("0C" to "1000 rpm"))
        repository.endSession(sessionId)

        val detail = repository.getDetail(sessionId)
        assertNotNull(detail)
        assertEquals(1, detail!!.dtcSnapshots.size)
        assertEquals("P0133", detail.freezeFrames.first().dtcCode)

        val json = repository.exportJson(sessionId)
        assertNotNull(json)
        assertEquals(true, json!!.contains("P0133"))
    }
}
