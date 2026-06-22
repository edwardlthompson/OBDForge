package dev.foss.obdforge.data.diagnostics

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DiagnosticLogFileExporterTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun writeExportFilesCreatesLatestCopy() {
        val file = DiagnosticLogFileExporter.writeExportFiles(context, """{"version":1}""")
        assertTrue(file.exists())
        val latest = File(DiagnosticLogFileExporter.exportDirectory(context), "latest-diagnostic-log.json")
        assertTrue(latest.exists())
        assertEquals("""{"version":1}""", latest.readText())
    }

    @Test
    fun buildShareIntentTargetsJson() {
        val uri = Uri.parse("content://dev.foss.obdforge.fileprovider/diagnostic_logs/test.json")
        val intent = DiagnosticLogFileExporter.buildShareIntent(uri)
        assertEquals(Intent.ACTION_SEND, intent.action)
        assertEquals("application/json", intent.type)
        assertTrue(intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0)
    }
}
