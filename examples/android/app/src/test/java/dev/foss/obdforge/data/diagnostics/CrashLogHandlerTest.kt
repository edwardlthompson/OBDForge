package dev.foss.obdforge.data.diagnostics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.persistence.DiagnosticEventRepository
import androidx.room.Room
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventCategory
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
class CrashLogHandlerTest {
    private lateinit var context: Context
    private lateinit var database: ObdForgeDatabase
    private lateinit var repository: DiagnosticEventRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        repository = DiagnosticEventRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun pendingCrashImportsOnNextLaunch() = runTest {
        CrashLogHandler.writePendingCrash(
            context,
            threadName = "main",
            throwable = IllegalStateException("bench failure"),
        )
        CrashLogHandler.drainPendingCrash(context, repository)
        assertEquals(1, repository.count())
        val record = repository.allRecords().single()
        assertEquals(DiagnosticEventCategory.Crash, record.category)
    }
}
