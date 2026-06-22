package dev.foss.obdforge.ui.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
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
import dev.foss.goldenpath.R
import dev.foss.goldenpath.ui.theme.SpacingMd
import dev.foss.obdforge.domain.ai.AiExplanationSource
import dev.foss.obdforge.domain.ai.DtcExplanation

@Composable
fun DtcExplainScreen(
    defaultCode: String,
    llmBundled: Boolean,
    classifierBundled: Boolean,
    explanation: DtcExplanation?,
    statusMessage: String?,
    loading: Boolean,
    onExplain: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var dtcCode by remember(defaultCode) { mutableStateOf(defaultCode) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.ai_dtc_explain_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.ai_offline_notice),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(
                R.string.ai_engine_status,
                if (llmBundled) stringResource(R.string.ai_engine_ready)
                else stringResource(R.string.ai_engine_catalog_fallback),
                if (classifierBundled) stringResource(R.string.ai_engine_ready)
                else stringResource(R.string.ai_engine_optional_off),
            ),
            style = MaterialTheme.typography.bodySmall,
        )
        OutlinedTextField(
            value = dtcCode,
            onValueChange = { dtcCode = it.uppercase() },
            label = { Text(stringResource(R.string.ai_dtc_code_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
        )
        Button(
            onClick = { onExplain(dtcCode) },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                stringResource(
                    if (loading) R.string.ai_dtc_explain_loading else R.string.ai_dtc_explain_submit,
                ),
            )
        }
        explanation?.let { result ->
            AssistChip(
                onClick = {},
                enabled = false,
                label = {
                    Text(stringResource(sourceLabelRes(result.source)))
                },
            )
            Text(text = result.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = result.summary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        statusMessage?.let { message ->
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.ai_dtc_explain_back))
        }
    }
}

private fun sourceLabelRes(source: AiExplanationSource): Int = when (source) {
    AiExplanationSource.Catalog -> R.string.ai_source_catalog
    AiExplanationSource.MediaPipe -> R.string.ai_source_mediapipe
}
