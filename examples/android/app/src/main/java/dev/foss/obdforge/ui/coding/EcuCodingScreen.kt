package dev.foss.obdforge.ui.coding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.obdBottomGutter

@Composable
fun EcuCodingScreen(
    statusMessage: String,
    busy: Boolean,
    onRead: (didHex: String) -> Unit,
    onWrite: (didHex: String, dataHex: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var didHex by remember { mutableStateOf("F190") }
    var dataHex by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .obdBottomGutter(),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.ecu_coding_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.ecu_coding_warning),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
        OutlinedTextField(
            value = didHex,
            onValueChange = { didHex = it },
            label = { Text(stringResource(R.string.ecu_coding_did_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !busy,
        )
        OutlinedTextField(
            value = dataHex,
            onValueChange = { dataHex = it },
            label = { Text(stringResource(R.string.ecu_coding_data_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !busy,
        )
        Button(
            onClick = { onRead(didHex) },
            enabled = !busy && didHex.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.ecu_coding_read))
        }
        Button(
            onClick = { onWrite(didHex, dataHex) },
            enabled = !busy && didHex.isNotBlank() && dataHex.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.ecu_coding_write))
        }
        if (statusMessage.isNotBlank()) {
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.ecu_coding_back))
        }
    }
}
