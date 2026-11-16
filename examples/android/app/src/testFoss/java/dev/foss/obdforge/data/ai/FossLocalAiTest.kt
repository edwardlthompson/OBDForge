package dev.foss.obdforge.data.ai

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.BuildConfig
import dev.foss.obdforge.data.demo.DemoObdFixtures
import dev.foss.obdforge.domain.ai.AiExplanationSource
import dev.foss.obdforge.domain.ai.ExplainDtcOutcome
import dev.foss.obdforge.domain.livedata.PersonaMode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FossLocalAiTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun fossFlavor_disablesOnDeviceLlm() = runTest {
        assertTrue(BuildConfig.FDROID)
        assertFalse(BuildConfig.LOCAL_AI_ENABLED)
        assertFalse(LlmModelProvisioner.isModelPresent(context))
        assertTrue(LlmModelProvisioner.downloadModel(context).isFailure)

        val outcome = LocalAiEngineFactory.createExplainDtcUseCase(context)
            .explain(DemoObdFixtures.PRIMARY_DTC, PersonaMode.Diy)
        assertTrue(outcome is ExplainDtcOutcome.Success)
        val explanation = (outcome as ExplainDtcOutcome.Success).explanation
        assertEquals(AiExplanationSource.Catalog, explanation.source)
    }
}
