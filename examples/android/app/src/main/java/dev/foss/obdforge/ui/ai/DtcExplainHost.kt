package dev.foss.obdforge.ui.ai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.obdforge.R
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.ai.LlmModelProvisioner
import dev.foss.obdforge.data.ai.MediaPipeLlmEngine
import dev.foss.obdforge.data.ai.TfliteDtcClassifier
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.domain.livedata.PersonaMode
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DtcExplainHost(
    root: ObdForgeCompositionRoot,
    scope: CoroutineScope,
    persona: PersonaMode,
    transportSelection: TransportSelection,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coordinator = remember(root, transportSelection) {
        DtcExplainCoordinator(
            explainDtcUseCase = root.explainDtcUseCase,
            vehicleHealthScanUseCase = root.vehicleHealthScanUseCase,
            vinProfileRepository = root.vinProfileRepository,
            transportSelection = transportSelection,
        )
    }
    val snapshot by coordinator.snapshot.collectAsStateWithLifecycle(initialValue = null)
    val selectedCode by coordinator.selectedCode.collectAsStateWithLifecycle(initialValue = null)
    val explanation by coordinator.explanation.collectAsStateWithLifecycle(initialValue = null)
    val statusKey by coordinator.statusMessage.collectAsStateWithLifecycle(initialValue = null)
    val scanning by coordinator.scanning.collectAsStateWithLifecycle(initialValue = false)
    val loading by coordinator.loading.collectAsStateWithLifecycle(initialValue = false)
    var llmBundled by remember { mutableStateOf(MediaPipeLlmEngine.resolveModelPath(context) != null) }
    var llmDownloadProgress by remember { mutableFloatStateOf(-1f) }
    val classifierBundled = remember { TfliteDtcClassifier.isAssetBundled(context) }

    LaunchedEffect(persona, transportSelection) {
        coordinator.scanVehicle(persona)
    }

    val statusMessage = statusKey?.let { key ->
        when (key) {
            DtcExplainCoordinator.STATUS_INVALID_CODE ->
                stringResource(R.string.ai_error_invalid_dtc)
            DtcExplainCoordinator.STATUS_UNAVAILABLE ->
                stringResource(R.string.ai_error_unavailable)
            DtcExplainCoordinator.STATUS_SCAN_FAILED ->
                stringResource(R.string.ai_error_scan_failed)
            else -> key
        }
    }

    DtcExplainScreen(
        snapshot = snapshot,
        selectedCode = selectedCode,
        explanation = explanation,
        llmBundled = llmBundled,
        classifierBundled = classifierBundled,
        llmDownloadProgress = llmDownloadProgress.takeIf { it >= 0f },
        onDownloadLlm = {
            scope.launch {
                llmDownloadProgress = 0f
                LlmModelProvisioner.downloadModel(context) { llmDownloadProgress = it }
                    .onSuccess { llmBundled = true }
                llmDownloadProgress = -1f
            }
        },
        statusMessage = statusMessage,
        scanning = scanning,
        loading = loading,
        onSelectCode = coordinator::selectCode,
        onRescan = { scope.launch { coordinator.scanVehicle(persona) } },
        onExplainManual = { code -> scope.launch { coordinator.explainManual(code, persona) } },
        onBack = {
            coordinator.clear()
            onBack()
        },
        modifier = modifier,
    )
}
