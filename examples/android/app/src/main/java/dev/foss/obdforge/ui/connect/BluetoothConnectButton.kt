package dev.foss.obdforge.ui.connect

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R

@Composable
fun BluetoothConnectButton(
    enabled: Boolean,
    isConnecting: Boolean,
    lastAdapterLabel: String?,
    onConnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onConnect,
        enabled = enabled && !isConnecting,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = when {
                isConnecting -> stringResource(R.string.bluetooth_connecting)
                lastAdapterLabel != null -> stringResource(R.string.bluetooth_connect_to, lastAdapterLabel)
                else -> stringResource(R.string.bluetooth_connect)
            },
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
