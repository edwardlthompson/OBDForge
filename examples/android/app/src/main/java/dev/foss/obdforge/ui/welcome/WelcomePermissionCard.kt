package dev.foss.obdforge.ui.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.SpacingSm

@Composable
fun WelcomePermissionCard(
    title: String,
    description: String,
    granted: Boolean,
    grantable: Boolean,
    onGrant: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingSm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (granted) {
                        stringResource(R.string.welcome_permission_status_granted)
                    } else {
                        stringResource(R.string.welcome_permission_status_needed)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (granted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (grantable && !granted) {
                Button(onClick = onGrant, modifier = Modifier.fillMaxWidth()) {
                    Text(actionLabel ?: stringResource(R.string.welcome_permission_grant))
                }
            }
        }
    }
}

@Composable
fun WelcomeUsbPermissionCard(
    device: WelcomeUsbDeviceState,
    onGrant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingSm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = device.label, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = if (device.granted) {
                        stringResource(R.string.welcome_permission_status_granted)
                    } else {
                        stringResource(R.string.welcome_permission_status_needed)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (device.granted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            }
            if (!device.granted) {
                OutlinedButton(onClick = onGrant, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.welcome_usb_grant_device))
                }
            }
        }
    }
}
