package dev.foss.obdforge.ui.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingLg
import dev.foss.obdforge.ui.theme.SpacingMd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    bluetoothRequired: Boolean,
    bluetoothGranted: Boolean,
    cameraGranted: Boolean,
    usbDevices: List<WelcomeUsbDeviceState>,
    onGrantBluetooth: () -> Unit,
    onGrantCamera: () -> Unit,
    onGrantUsb: (deviceName: String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.welcome_title)) })
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingMd),
                verticalArrangement = Arrangement.spacedBy(SpacingMd),
            ) {
                Text(
                    text = stringResource(R.string.welcome_continue_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.welcome_continue))
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = SpacingMd)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Text(
                text = stringResource(R.string.welcome_intro),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = SpacingMd, bottom = SpacingLg),
            )
            WelcomePermissionCard(
                title = stringResource(R.string.welcome_permission_bluetooth_title),
                description = stringResource(R.string.welcome_permission_bluetooth_body),
                granted = bluetoothGranted,
                grantable = bluetoothRequired,
                onGrant = onGrantBluetooth,
            )
            WelcomePermissionCard(
                title = stringResource(R.string.welcome_permission_camera_title),
                description = stringResource(R.string.welcome_permission_camera_body),
                granted = cameraGranted,
                grantable = true,
                onGrant = onGrantCamera,
            )
            WelcomePermissionCard(
                title = stringResource(R.string.welcome_permission_network_title),
                description = stringResource(R.string.welcome_permission_network_body),
                granted = true,
                grantable = false,
                onGrant = {},
            )
            Text(
                text = stringResource(R.string.welcome_permission_usb_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.welcome_permission_usb_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (usbDevices.isEmpty()) {
                Text(
                    text = stringResource(R.string.welcome_usb_none_attached),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                usbDevices.forEach { device ->
                    WelcomeUsbPermissionCard(
                        device = device,
                        onGrant = { onGrantUsb(device.deviceName) },
                    )
                }
            }
        }
    }
}
