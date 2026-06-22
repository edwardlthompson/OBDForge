package dev.foss.obdforge.data.diagnostics

import android.content.Context
import dev.foss.obdforge.BuildConfig
import dev.foss.obdforge.data.persistence.DiagnosticEventRepository
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventCategory
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventSeverity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

object CrashLogHandler {
    private const val PENDING_FILE = "crash_pending.json"

    fun install(
        context: Context,
        repository: DiagnosticEventRepository,
        scope: CoroutineScope,
    ) {
        val appContext = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            writePendingCrash(appContext, thread.name, throwable)
            previous?.uncaughtException(thread, throwable)
        }
        scope.launch {
            drainPendingCrash(appContext, repository)
            purgeStaleEvents(repository)
        }
    }

    internal suspend fun drainPendingCrash(context: Context, repository: DiagnosticEventRepository) {
        importPendingCrash(context, repository)
    }

    internal fun writePendingCrash(context: Context, threadName: String, throwable: Throwable) {
        runCatching {
            val payload = JSONObject()
                .put("thread", threadName)
                .put("appVersion", BuildConfig.VERSION_NAME)
                .put("timestampEpochMs", System.currentTimeMillis())
                .put("message", DiagnosticLogSanitizer.sanitize(throwable.message ?: throwable.javaClass.simpleName))
                .put("stackTrace", DiagnosticLogSanitizer.stackTrace(throwable))
            pendingFile(context).writeText(payload.toString())
        }
    }

    private suspend fun importPendingCrash(context: Context, repository: DiagnosticEventRepository) {
        val file = pendingFile(context)
        if (!file.exists()) return
        runCatching {
            val payload = JSONObject(file.readText())
            repository.record(
                category = DiagnosticEventCategory.Crash,
                severity = DiagnosticEventSeverity.Error,
                message = payload.optString("message", "Uncaught exception"),
                detail = payload.optString("stackTrace"),
                timestampEpochMs = payload.optLong("timestampEpochMs", System.currentTimeMillis()),
            )
        }
        file.delete()
    }

    private suspend fun purgeStaleEvents(repository: DiagnosticEventRepository) {
        val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(
            DiagnosticEventRepository.RETENTION_DAYS.toLong(),
        )
        repository.purgeOlderThan(cutoff)
    }

    private fun pendingFile(context: Context): File =
        File(context.filesDir, PENDING_FILE)
}
