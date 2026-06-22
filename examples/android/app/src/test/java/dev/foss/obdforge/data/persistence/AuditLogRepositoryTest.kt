package dev.foss.obdforge.data.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
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
class AuditLogRepositoryTest {
    private lateinit var database: ObdForgeDatabase
    private lateinit var repository: AuditLogRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        repository = AuditLogRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun recordAndExportJson() = runTest {
        repository.recordAttempt(
            persona = "Racing",
            protocolId = "stn",
            commandType = "UdsWrite",
            commandHash = "abc123",
            outcome = "blocked:ExpertModeRequired",
            userNote = "bench test",
            timestampEpochMs = 500L,
        )
        assertEquals(1, repository.count())
        val json = repository.exportJson()
        val root = JSONObject(json)
        assertEquals(1, root.getJSONArray("entries").length())
        assertTrue(json.contains("abc123"))
    }
}
