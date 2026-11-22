package dev.foss.obdforge.ui.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.SpacingSm

@Composable
internal fun LocalAiBanner(
    llmBundled: Boolean,
    llmDownloadProgress: Float?,
    onDownloadLlm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (llmBundled) return
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingSm),
        ) {
            Text(
                text = stringResource(R.string.ai_llm_download_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (llmDownloadProgress != null) {
                LinearProgressIndicator(
                    progress = { llmDownloadProgress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(
                        R.string.ai_llm_download_progress,
                        (llmDownloadProgress * 100).toInt(),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )
            } else {
                Button(onClick = onDownloadLlm, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.ai_llm_download_button))
                }
            }
        }
    }
}
