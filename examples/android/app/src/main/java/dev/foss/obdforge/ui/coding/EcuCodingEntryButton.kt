package dev.foss.obdforge.ui.coding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R

@Composable
fun EcuCodingEntryButton(
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(onClick = onOpen, modifier = modifier.fillMaxWidth()) {
        Text(stringResource(R.string.ecu_coding_open))
    }
}
