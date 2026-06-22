package dev.foss.obdforge.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import dev.foss.goldenpath.ui.theme.SpacingMd
import dev.foss.goldenpath.ui.theme.SpacingSm
import dev.foss.obdforge.domain.session.SessionDetail

@Composable
fun SessionDetailPanel(
    detail: SessionDetail,
    exportJson: String?,
    onExport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpacingSm),
    ) {
        Text(
            text = stringResource(R.string.session_detail_title),
            style = MaterialTheme.typography.titleMedium,
        )
        detail.dtcSnapshots.forEach { snapshot ->
            Text(
                text = stringResource(
                    R.string.session_detail_dtc_line,
                    snapshot.codes.joinToString(", ").ifEmpty { "—" },
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        detail.freezeFrames.forEach { frame ->
            Text(
                text = stringResource(
                    R.string.session_detail_freeze_line,
                    frame.dtcCode,
                    frame.pidValues.entries.joinToString { "${it.key}=${it.value}" },
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.session_export_json))
        }
        exportJson?.let { json ->
            Text(
                text = json,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
                    .verticalScroll(rememberScrollState()),
            )
        }
    }
}
