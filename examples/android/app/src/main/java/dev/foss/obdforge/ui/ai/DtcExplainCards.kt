package dev.foss.obdforge.ui.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.SpacingSm
import dev.foss.obdforge.domain.ai.AiExplanationSource
import dev.foss.obdforge.domain.ai.DtcExplanation
import dev.foss.obdforge.domain.diagnostics.AbnormalPidReading
import dev.foss.obdforge.domain.diagnostics.AbnormalReason

@Composable
internal fun AbnormalPidCard(reading: AbnormalPidReading) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingSm),
        ) {
            Text(
                text = reading.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = stringResource(
                    if (reading.reason == AbnormalReason.AboveMax) {
                        R.string.ai_dtc_out_of_range_high
                    } else {
                        R.string.ai_dtc_out_of_range_low
                    },
                    reading.formattedValue,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}

@Composable
internal fun DtcExplanationCard(explanation: DtcExplanation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingSm),
        ) {
            AssistChip(
                onClick = {},
                enabled = false,
                label = { Text(stringResource(sourceLabelRes(explanation.source))) },
            )
            Text(
                text = explanation.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = explanation.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

internal fun sourceLabelRes(source: AiExplanationSource): Int = when (source) {
    AiExplanationSource.Catalog -> R.string.ai_source_catalog
    AiExplanationSource.MediaPipe -> R.string.ai_source_mediapipe
}

@Composable
internal fun LlmDownloadCard(
    downloadProgress: Float?,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            if (downloadProgress != null) {
                LinearProgressIndicator(
                    progress = { downloadProgress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(
                        R.string.ai_llm_download_progress,
                        (downloadProgress * 100).toInt(),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )
            } else {
                Button(onClick = onDownload, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.ai_llm_download_button))
                }
            }
        }
    }
}
