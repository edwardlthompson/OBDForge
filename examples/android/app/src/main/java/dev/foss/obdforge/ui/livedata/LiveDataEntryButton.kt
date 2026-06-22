package dev.foss.obdforge.ui.livedata

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R

@Composable
fun LiveDataEntryButton(
    enabled: Boolean,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onOpen,
        enabled = enabled,
        modifier = modifier,
    ) {
        Text(stringResource(R.string.livedata_open))
    }
}
