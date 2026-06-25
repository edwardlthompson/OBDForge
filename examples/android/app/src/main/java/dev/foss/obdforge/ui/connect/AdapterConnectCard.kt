package dev.foss.obdforge.ui.connect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.SpacingSm

@Composable
fun AdapterConnectCard(
    isConnected: Boolean,
    isConnecting: Boolean,
    adapterSummary: String?,
    transportLabel: String?,
    vinDisplay: String,
    vinSourceLabel: String,
    statusMessage: String,
    canConnect: Boolean,
    showSetupHint: Boolean,
    onConnect: () -> Unit,
    onChangeAdapter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
    ) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingSm),
        ) {
            Text(
                text = stringResource(R.string.adapter_connect_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = when {
                    isConnecting -> stringResource(R.string.bluetooth_connecting)
                    isConnected -> stringResource(R.string.connection_status_connected_adapter)
                    else -> stringResource(R.string.connection_status_disconnected)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val savedLabel = listOfNotNull(transportLabel, adapterSummary)
                .filter { it.isNotBlank() }
                .joinToString(" · ")
            if (savedLabel.isNotBlank()) {
                Text(
                    text = stringResource(R.string.adapter_connect_saved_label, savedLabel),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else if (showSetupHint) {
                Text(
                    text = stringResource(R.string.adapter_connect_setup_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (isConnected) {
                Text(
                    text = vinDisplay,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (vinSourceLabel.isNotBlank()) {
                    Text(
                        text = vinSourceLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Button(
                onClick = onConnect,
                enabled = canConnect && !isConnecting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = when {
                        isConnecting -> stringResource(R.string.bluetooth_connecting)
                        showSetupHint -> stringResource(R.string.adapter_connect_choose)
                        isConnected -> stringResource(R.string.adapter_connect_reconnect)
                        adapterSummary != null -> stringResource(R.string.bluetooth_connect_to, adapterSummary)
                        else -> stringResource(R.string.bluetooth_connect)
                    },
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            OutlinedButton(
                onClick = onChangeAdapter,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.adapter_connect_change))
            }
            if (statusMessage.isNotBlank()) {
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
