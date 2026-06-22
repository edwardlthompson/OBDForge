package dev.foss.obdforge.data.demo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.ai.LocalAiEngineFactory
import dev.foss.obdforge.data.ai.MediaPipeLlmEngine
import dev.foss.obdforge.domain.ai.AiExplanationSource
import dev.foss.obdforge.domain.ai.ExplainDtcOutcome
import dev.foss.obdforge.domain.livedata.PersonaMode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DemoAiFlowTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun airplaneModeExplainDemoDtc_usesOfflineCatalogWithoutModel() = runTest {
        assertTrue(MediaPipeLlmEngine.resolveModelPath(context) == null)
        val useCase = LocalAiEngineFactory.createExplainDtcUseCase(context)
        val outcome = useCase.explain(DemoObdFixtures.PRIMARY_DTC, PersonaMode.Diy)
        assertTrue(outcome is ExplainDtcOutcome.Success)
        val explanation = (outcome as ExplainDtcOutcome.Success).explanation
        assertTrue(explanation.source == AiExplanationSource.Catalog)
        assertFalse(explanation.summary.contains("http", ignoreCase = true))
    }
}
