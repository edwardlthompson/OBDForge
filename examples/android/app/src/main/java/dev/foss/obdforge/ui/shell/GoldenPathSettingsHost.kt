package dev.foss.obdforge.ui.shell

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.settings.SettingsScreen
import dev.foss.obdforge.ui.theme.ThemeMode
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.persona.PersonaNavigation
import dev.foss.obdforge.ui.settings.SettingsDiagnosticsCoordinator
import dev.foss.obdforge.ui.settings.SettingsSafetyCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

@Composable
fun GoldenPathSettingsHost(
    context: Context,
    scope: CoroutineScope,
    root: ObdForgeCompositionRoot,
    themeMode: ThemeMode,
    personaMode: PersonaMode,
    updateCheckEnabled: Boolean,
    demoModeEnabled: Boolean,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onUpdateCheckChange: (Boolean) -> Unit,
    onDemoModeChange: (Boolean) -> Unit,
    onPersonaChange: (PersonaMode) -> Unit,
    onBack: () -> Unit,
    onReviewPermissions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coordinator = androidx.compose.runtime.remember(root) {
        SettingsSafetyCoordinator(root)
    }
    val diagnosticsCoordinator = androidx.compose.runtime.remember(context, root) {
        SettingsDiagnosticsCoordinator(context, root)
    }
    val unlockExpiresAtMs by coordinator.unlockExpiresAtMs.collectAsStateWithLifecycle(initialValue = null)
    val auditEntryCount by coordinator.auditEntryCount.collectAsStateWithLifecycle(initialValue = 0)
    val auditExportJson by coordinator.auditExportJson.collectAsStateWithLifecycle(initialValue = null)
    val expertPinErrorKey by coordinator.expertPinError.collectAsStateWithLifecycle(initialValue = null)
    val diagnosticLoggingEnabled by diagnosticsCoordinator.loggingEnabled.collectAsStateWithLifecycle(
        initialValue = dev.foss.obdforge.data.preferences.DiagnosticLogPreferences.DEFAULT_ENABLED,
    )
    val diagnosticEntryCount by diagnosticsCoordinator.entryCount.collectAsStateWithLifecycle(initialValue = 0)
    val diagnosticExportStatus by diagnosticsCoordinator.exportStatusMessage.collectAsStateWithLifecycle(
        initialValue = null,
    )

    LaunchedEffect(coordinator, diagnosticsCoordinator) {
        coordinator.refreshAuditCount()
        diagnosticsCoordinator.refreshEntryCount()
    }

    val expertUnlocked = coordinator.isExpertUnlocked(unlockExpiresAtMs)
    val expiresAt = unlockExpiresAtMs
    val unlockStatusMessage = if (expertUnlocked && expiresAt != null) {
        val formatted = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(expiresAt))
        context.getString(R.string.settings_expert_unlocked_until, formatted)
    } else {
        context.getString(R.string.settings_expert_locked)
    }
    val pinErrorMessage = expertPinErrorKey?.let {
        context.getString(R.string.settings_expert_pin_invalid)
    }

    SettingsScreen(
        themeMode = themeMode,
        personaMode = personaMode,
        updateCheckEnabled = updateCheckEnabled,
        demoModeEnabled = demoModeEnabled,
        showExpertSection = PersonaNavigation.showsExpertMode(personaMode),
        showAuditSection = PersonaNavigation.showsAuditExport(personaMode),
        expertUnlocked = expertUnlocked,
        expertUnlockStatusMessage = unlockStatusMessage,
        expertPinErrorMessage = pinErrorMessage,
        auditEntryCount = auditEntryCount,
        auditExportJson = auditExportJson,
        diagnosticLoggingEnabled = diagnosticLoggingEnabled,
        diagnosticEntryCount = diagnosticEntryCount,
        diagnosticExportPathHint = diagnosticsCoordinator.exportPathHint(),
        diagnosticExportStatusMessage = diagnosticExportStatus,
        onThemeModeSelect = onThemeModeSelect,
        onPersonaSelect = { mode -> scope.launch { onPersonaChange(mode) } },
        onUpdateCheckChange = onUpdateCheckChange,
        onDemoModeChange = onDemoModeChange,
        onExpertUnlock = { pin -> scope.launch { coordinator.unlockExpert(pin) } },
        onExpertLock = { scope.launch { coordinator.lockExpert() } },
        onAuditExport = {
            scope.launch {
                coordinator.exportAuditLog()
                coordinator.refreshAuditCount()
            }
        },
        onDiagnosticLoggingEnabledChange = { enabled ->
            scope.launch { diagnosticsCoordinator.setLoggingEnabled(enabled) }
        },
        onDiagnosticExport = {
            scope.launch { diagnosticsCoordinator.exportAndShare() }
        },
        onReviewPermissions = onReviewPermissions,
        onBack = onBack,
        modifier = modifier,
    )
}
