package dev.foss.obdforge.data.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventCategory
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventSeverity
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
class DiagnosticEventRepositoryTest {
    private lateinit var database: ObdForgeDatabase
    private lateinit var repository: DiagnosticEventRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        repository = DiagnosticEventRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun recordAndExportJson() = runTest {
        repository.record(
            category = DiagnosticEventCategory.Connection,
            severity = DiagnosticEventSeverity.Error,
            message = "Connect failed: timeout",
            transportType = "Bluetooth",
            timestampEpochMs = 900L,
        )
        assertEquals(1, repository.count())
        val root = JSONObject(repository.exportJson())
        assertEquals(1, root.getJSONArray("entries").length())
        assertTrue(root.getString("appVersion").isNotEmpty())
    }
}
