package dev.foss.obdforge.ui.shop

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
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.obdBottomGutter

@Composable
fun ShopIntakeScreen(
    defaultVin: String,
    statusMessage: String?,
    onSubmit: (customerName: String, phone: String, vin: String, notes: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var customerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf(defaultVin) }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .obdBottomGutter()
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.shop_intake_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text(stringResource(R.string.shop_customer_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.shop_customer_phone)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = vin,
            onValueChange = { vin = it.uppercase() },
            label = { Text(stringResource(R.string.shop_vin_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text(stringResource(R.string.shop_intake_notes)) },
            modifier = Modifier.fillMaxWidth(),
        )
        statusMessage?.let { message ->
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }
        Button(
            onClick = { onSubmit(customerName, phone, vin, notes) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.shop_intake_submit))
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.shop_back))
        }
    }
}
