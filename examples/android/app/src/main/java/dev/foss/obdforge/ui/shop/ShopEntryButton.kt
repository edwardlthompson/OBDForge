package dev.foss.obdforge.ui.shop

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R

@Composable
fun ShopEntryButton(
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(onClick = onOpen, modifier = modifier) {
        Text(stringResource(R.string.shop_open))
    }
}
