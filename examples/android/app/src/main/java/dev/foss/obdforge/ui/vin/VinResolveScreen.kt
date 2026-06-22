package dev.foss.obdforge.ui.vin

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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import dev.foss.goldenpath.R
import dev.foss.goldenpath.ui.theme.SpacingMd
import dev.foss.obdforge.domain.vehicle.VehicleProfile
import dev.foss.obdforge.domain.vehicle.VinValidationError

@Composable
fun VinResolveScreen(
    latestProfile: VehicleProfile?,
    ecuResolveEnabled: Boolean,
    statusMessage: String?,
    showCamera: Boolean,
    onManualSubmit: (String) -> Unit,
    onResolveFromEcu: () -> Unit,
    onOpenScanner: () -> Unit,
    onCloseScanner: () -> Unit,
    onBarcodeScanned: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var manualVin by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.vin_resolve_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        latestProfile?.let { profile ->
            Text(text = stringResource(R.string.vin_profile_label, profile.vin))
            VinBadge(source = profile.source)
        }
        if (showCamera) {
            VinBarcodeCamera(
                onBarcodeDetected = onBarcodeScanned,
                onClose = onCloseScanner,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        } else {
            OutlinedTextField(
                value = manualVin,
                onValueChange = { manualVin = it.uppercase() },
                label = { Text(stringResource(R.string.vin_manual_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            )
            Button(
                onClick = { onManualSubmit(manualVin) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.vin_manual_save))
            }
            Button(
                onClick = onResolveFromEcu,
                enabled = ecuResolveEnabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.vin_resolve_ecu))
            }
            Button(
                onClick = onOpenScanner,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.vin_scan_barcode))
            }
        }
        statusMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (!showCamera) {
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.vin_resolve_back))
            }
        }
    }
}

fun validationErrorMessageRes(error: VinValidationError): Int = when (error) {
    VinValidationError.WrongLength -> R.string.vin_error_length
    VinValidationError.InvalidCharacters -> R.string.vin_error_charset
    VinValidationError.CheckDigitMismatch -> R.string.vin_error_check_digit
}
