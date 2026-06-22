package dev.foss.obdforge.ui.session

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R

@Composable
fun SessionHistoryEntryButton(
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(onClick = onOpen, modifier = modifier) {
        Text(stringResource(R.string.session_history_open))
    }
}
