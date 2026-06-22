package dev.foss.obdforge.ui.ai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.goldenpath.R
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.ai.MediaPipeLlmEngine
import dev.foss.obdforge.data.ai.TfliteDtcClassifier
import dev.foss.obdforge.data.demo.DemoObdFixtures
import dev.foss.obdforge.domain.livedata.PersonaMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DtcExplainHost(
    root: ObdForgeCompositionRoot,
    scope: CoroutineScope,
    persona: PersonaMode,
    defaultCode: String = DemoObdFixtures.PRIMARY_DTC,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coordinator = remember(root) { DtcExplainCoordinator(root.explainDtcUseCase) }
    val explanation by coordinator.explanation.collectAsStateWithLifecycle(initialValue = null)
    val statusKey by coordinator.statusMessage.collectAsStateWithLifecycle(initialValue = null)
    val loading by coordinator.loading.collectAsStateWithLifecycle(initialValue = false)
    val llmBundled = remember { MediaPipeLlmEngine.resolveModelPath(context) != null }
    val classifierBundled = remember { TfliteDtcClassifier.isAssetBundled(context) }

    val statusMessage = statusKey?.let { key ->
        when (key) {
            DtcExplainCoordinator.STATUS_INVALID_CODE ->
                stringResource(R.string.ai_error_invalid_dtc)
            DtcExplainCoordinator.STATUS_UNAVAILABLE ->
                stringResource(R.string.ai_error_unavailable)
            else -> key
        }
    }

    DtcExplainScreen(
        defaultCode = defaultCode,
        llmBundled = llmBundled,
        classifierBundled = classifierBundled,
        explanation = explanation,
        statusMessage = statusMessage,
        loading = loading,
        onExplain = { code -> scope.launch { coordinator.explain(code, persona) } },
        onBack = {
            coordinator.clear()
            onBack()
        },
        modifier = modifier,
    )
}
