package dev.foss.goldenpath.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R
import dev.foss.goldenpath.ui.theme.SpacingMd

@Composable
fun SettingsExpertModeSection(
    expertUnlocked: Boolean,
    unlockStatusMessage: String,
    pinErrorMessage: String?,
    onUnlock: (String) -> Unit,
    onLock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pin by remember { mutableStateOf("") }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.settings_expert_mode_label),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.settings_expert_mode_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = unlockStatusMessage,
            style = MaterialTheme.typography.bodyMedium,
        )
        if (!expertUnlocked) {
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text(stringResource(R.string.settings_expert_pin_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            pinErrorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Button(
                onClick = {
                    onUnlock(pin)
                    pin = ""
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.settings_expert_unlock))
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(onClick = onLock) {
                    Text(stringResource(R.string.settings_expert_lock))
                }
            }
        }
    }
}

@Composable
fun SettingsAuditExportSection(
    auditEntryCount: Int,
    exportJson: String?,
    onExport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.settings_audit_log_label),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.settings_audit_log_count, auditEntryCount),
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.settings_audit_export))
        }
        exportJson?.let { json ->
            Text(
                text = json,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SpacingMd),
            )
        }
    }
}
