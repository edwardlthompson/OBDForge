package dev.foss.obdforge.ui.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import dev.foss.goldenpath.ui.theme.SpacingSm
import dev.foss.obdforge.domain.ai.AiExplanationSource
import dev.foss.obdforge.domain.ai.DtcExplanation
import dev.foss.obdforge.domain.diagnostics.AbnormalPidReading
import dev.foss.obdforge.domain.diagnostics.AbnormalReason
import dev.foss.obdforge.domain.diagnostics.VehicleHealthSnapshot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DtcExplainScreen(
    snapshot: VehicleHealthSnapshot?,
    selectedCode: String?,
    explanation: DtcExplanation?,
    llmBundled: Boolean,
    classifierBundled: Boolean,
    statusMessage: String?,
    scanning: Boolean,
    loading: Boolean,
    onSelectCode: (String) -> Unit,
    onRescan: () -> Unit,
    onExplainManual: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var manualCode by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ai_dtc_explain_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.ai_dtc_explain_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRescan, enabled = !scanning) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.ai_dtc_rescan),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = SpacingMd),
            contentPadding = PaddingValues(vertical = SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            item {
                Text(
                    text = stringResource(R.string.ai_offline_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                Text(
                    text = stringResource(
                        R.string.ai_engine_status,
                        if (llmBundled) stringResource(R.string.ai_engine_ready)
                        else stringResource(R.string.ai_engine_catalog_fallback),
                        if (classifierBundled) stringResource(R.string.ai_engine_ready)
                        else stringResource(R.string.ai_engine_optional_off),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                Text(
                    text = stringResource(
                        if (scanning) R.string.ai_dtc_scanning else R.string.ai_dtc_scan_ready,
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            item {
                Text(
                    text = stringResource(R.string.ai_dtc_active_codes),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            if (snapshot?.dtcs.isNullOrEmpty() && !scanning) {
                item {
                    Text(
                        text = stringResource(R.string.ai_dtc_no_codes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(snapshot?.dtcs.orEmpty(), key = { it }) { code ->
                    FilterChip(
                        selected = code == selectedCode,
                        onClick = { onSelectCode(code) },
                        label = { Text(code) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            item {
                Text(
                    text = stringResource(R.string.ai_dtc_out_of_range),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            if (snapshot?.abnormalPids.isNullOrEmpty() && !scanning) {
                item {
                    Text(
                        text = stringResource(R.string.ai_dtc_no_abnormal),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(snapshot?.abnormalPids.orEmpty(), key = { it.pid }) { reading ->
                    AbnormalPidCard(reading = reading)
                }
            }
            explanation?.let { result ->
                item {
                    DtcExplanationCard(explanation = result)
                }
            }
            statusMessage?.let { message ->
                item {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item {
                Text(
                    text = stringResource(R.string.ai_dtc_manual_section),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            item {
                OutlinedTextField(
                    value = manualCode,
                    onValueChange = { manualCode = it.uppercase() },
                    label = { Text(stringResource(R.string.ai_dtc_code_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                )
            }
            item {
                Button(
                    onClick = { onExplainManual(manualCode) },
                    enabled = !loading && !scanning && manualCode.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        stringResource(
                            if (loading) R.string.ai_dtc_explain_loading else R.string.ai_dtc_explain_submit,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun AbnormalPidCard(reading: AbnormalPidReading) {
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
private fun DtcExplanationCard(explanation: DtcExplanation) {
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

private fun sourceLabelRes(source: AiExplanationSource): Int = when (source) {
    AiExplanationSource.Catalog -> R.string.ai_source_catalog
    AiExplanationSource.MediaPipe -> R.string.ai_source_mediapipe
}
