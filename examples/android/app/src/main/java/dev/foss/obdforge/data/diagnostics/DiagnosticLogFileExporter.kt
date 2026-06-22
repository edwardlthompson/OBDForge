package dev.foss.obdforge.data.diagnostics

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DiagnosticLogFileExporter {
    private const val LOG_DIR = "logs"
    private const val LATEST_NAME = "latest-diagnostic-log.json"
    private const val MIME_TYPE = "application/json"

    fun writeExportFiles(context: Context, json: String): File {
        val dir = exportDirectory(context)
        dir.mkdirs()
        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val stamped = File(dir, "obdforge-diagnostic-log-$timestamp.json")
        stamped.writeText(json)
        File(dir, LATEST_NAME).writeText(json)
        return stamped
    }

    fun exportDirectory(context: Context): File =
        File(context.getExternalFilesDir(null), LOG_DIR)

    fun buildShareIntent(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        return buildShareIntent(uri)
    }

    internal fun buildShareIntent(uri: Uri): Intent =
        Intent(Intent.ACTION_SEND).apply {
            type = MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

    fun launchShare(context: Context, file: File) {
        context.startActivity(
            Intent.createChooser(buildShareIntent(context, file), null),
        )
    }
}
