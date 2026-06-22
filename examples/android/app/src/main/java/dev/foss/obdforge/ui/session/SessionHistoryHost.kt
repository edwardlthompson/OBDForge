package dev.foss.obdforge.ui.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SessionHistoryHost(
    coordinator: SessionHistoryCoordinator,
    scope: CoroutineScope,
    onBack: () -> Unit,
) {
    val summaries by coordinator.summaries.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedDetail by coordinator.selectedDetail.collectAsStateWithLifecycle(initialValue = null)
    val exportJson by coordinator.exportJson.collectAsStateWithLifecycle(initialValue = null)
    val exportCsv by coordinator.exportCsv.collectAsStateWithLifecycle(initialValue = null)

    SessionHistoryScreen(
        summaries = summaries,
        selectedDetail = selectedDetail,
        exportJson = exportJson,
        exportCsv = exportCsv,
        onSelectSession = { sessionId -> scope.launch { coordinator.selectSession(sessionId) } },
        onExportJson = { scope.launch { coordinator.exportSelectedJson() } },
        onExportCsv = { scope.launch { coordinator.exportSelectedCsv() } },
        onBack = {
            coordinator.clearSelection()
            onBack()
        },
    )
}
