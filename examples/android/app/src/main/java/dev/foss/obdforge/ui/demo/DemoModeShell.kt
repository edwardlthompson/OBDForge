package dev.foss.obdforge.ui.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DemoModeShell(
    demoModeEnabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        if (demoModeEnabled) {
            DemoModeBanner()
        }
        content()
    }
}
