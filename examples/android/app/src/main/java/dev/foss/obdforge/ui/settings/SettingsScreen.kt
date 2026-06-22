package dev.foss.obdforge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.ThemeMode
import dev.foss.obdforge.domain.livedata.PersonaMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    personaMode: PersonaMode,
    updateCheckEnabled: Boolean,
    demoModeEnabled: Boolean,
    showExpertSection: Boolean,
    showAuditSection: Boolean,
    expertUnlocked: Boolean,
    expertUnlockStatusMessage: String,
    expertPinErrorMessage: String?,
    auditEntryCount: Int,
    auditExportJson: String?,
    diagnosticLoggingEnabled: Boolean,
    diagnosticEntryCount: Int,
    diagnosticExportPathHint: String,
    diagnosticExportStatusMessage: String?,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onPersonaSelect: (PersonaMode) -> Unit,
    onUpdateCheckChange: (Boolean) -> Unit,
    onDemoModeChange: (Boolean) -> Unit,
    onExpertUnlock: (String) -> Unit,
    onExpertLock: () -> Unit,
    onAuditExport: () -> Unit,
    onDiagnosticLoggingEnabledChange: (Boolean) -> Unit,
    onDiagnosticExport: () -> Unit,
    onReviewPermissions: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        SettingsPersonaSection(
            persona = personaMode,
            onPersonaSelect = onPersonaSelect,
        )
        Text(text = stringResource(R.string.settings_theme_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = themeMode == mode,
                    onClick = { onThemeModeSelect(mode) },
                    label = {
                        Text(
                            when (mode) {
                                ThemeMode.System -> stringResource(R.string.settings_theme_mode_system)
                                ThemeMode.Light -> stringResource(R.string.settings_theme_mode_light)
                                ThemeMode.Dark -> stringResource(R.string.settings_theme_mode_dark)
                            },
                        )
                    },
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Text(
                text = stringResource(R.string.settings_demo_mode_label),
                modifier = Modifier.weight(1f),
            )
            Switch(checked = demoModeEnabled, onCheckedChange = onDemoModeChange)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Text(
                text = stringResource(R.string.settings_update_check_label),
                modifier = Modifier.weight(1f),
            )
            Switch(checked = updateCheckEnabled, onCheckedChange = onUpdateCheckChange)
        }
        if (showExpertSection) {
            SettingsExpertModeSection(
                expertUnlocked = expertUnlocked,
                unlockStatusMessage = expertUnlockStatusMessage,
                pinErrorMessage = expertPinErrorMessage,
                onUnlock = onExpertUnlock,
                onLock = onExpertLock,
            )
        }
        if (showAuditSection) {
            SettingsAuditExportSection(
                auditEntryCount = auditEntryCount,
                exportJson = auditExportJson,
                onExport = onAuditExport,
            )
        }
        SettingsDiagnosticLogSection(
            loggingEnabled = diagnosticLoggingEnabled,
            entryCount = diagnosticEntryCount,
            exportPathHint = diagnosticExportPathHint,
            exportStatusMessage = diagnosticExportStatusMessage,
            onLoggingEnabledChange = onDiagnosticLoggingEnabledChange,
            onExport = onDiagnosticExport,
        )
        Button(onClick = onReviewPermissions, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.settings_review_permissions))
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.settings_close))
        }
    }
}
