package dev.foss.obdforge.ui.demo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R
import dev.foss.goldenpath.ui.theme.SpacingMd
import dev.foss.goldenpath.ui.theme.SpacingSm

@Composable
fun DemoModeBanner(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Text(
            text = stringResource(R.string.demo_mode_banner),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = SpacingMd, vertical = SpacingSm),
        )
    }
}
