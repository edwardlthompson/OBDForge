package dev.foss.obdforge.ui.ai

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R

@Composable
@Suppress("UNUSED_PARAMETER")
internal fun LocalAiBanner(
    llmBundled: Boolean,
    llmDownloadProgress: Float?,
    onDownloadLlm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.ai_llm_foss_note),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}
