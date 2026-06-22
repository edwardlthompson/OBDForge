package dev.foss.obdforge.ui.settings

import android.content.Context
import dev.foss.obdforge.R
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.diagnostics.DiagnosticLogFileExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsDiagnosticsCoordinator(
    private val context: Context,
    private val root: ObdForgeCompositionRoot,
) {
    val loggingEnabled = root.diagnosticLogPreferences.enabled

    private val _entryCount = MutableStateFlow(0)
    val entryCount: StateFlow<Int> = _entryCount.asStateFlow()

    private val _exportStatusMessage = MutableStateFlow<String?>(null)
    val exportStatusMessage: StateFlow<String?> = _exportStatusMessage.asStateFlow()

    fun exportPathHint(): String {
        val dir = DiagnosticLogFileExporter.exportDirectory(context)
        return context.getString(R.string.settings_diagnostic_log_path_hint, dir.absolutePath)
    }

    suspend fun refreshEntryCount() {
        _entryCount.value = root.diagnosticEventRepository.count()
    }

    suspend fun setLoggingEnabled(enabled: Boolean) {
        root.diagnosticLogPreferences.setEnabled(enabled)
        if (enabled) {
            root.diagnosticEventRecorder.recordNow(
                category = dev.foss.obdforge.domain.diagnostics.DiagnosticEventCategory.App,
                severity = dev.foss.obdforge.domain.diagnostics.DiagnosticEventSeverity.Info,
                message = "Diagnostic logging enabled",
            )
        }
    }

    suspend fun exportAndShare() {
        val json = root.diagnosticEventRepository.exportJson()
        val file = DiagnosticLogFileExporter.writeExportFiles(context, json)
        _exportStatusMessage.value = context.getString(
            R.string.settings_diagnostic_log_export_saved,
            file.name,
        )
        DiagnosticLogFileExporter.launchShare(context, file)
        refreshEntryCount()
    }
}
