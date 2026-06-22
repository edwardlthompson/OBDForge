package dev.foss.obdforge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd

@Composable
fun SettingsDiagnosticLogSection(
    loggingEnabled: Boolean,
    entryCount: Int,
    exportPathHint: String,
    exportStatusMessage: String?,
    onLoggingEnabledChange: (Boolean) -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.settings_diagnostic_log_label),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.settings_diagnostic_log_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Text(
                text = stringResource(R.string.settings_diagnostic_log_enabled),
                modifier = Modifier.weight(1f),
            )
            Switch(checked = loggingEnabled, onCheckedChange = onLoggingEnabledChange)
        }
        Text(
            text = stringResource(R.string.settings_diagnostic_log_count, entryCount),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = exportPathHint,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.settings_diagnostic_log_export))
        }
        exportStatusMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
