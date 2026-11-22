package dev.foss.obdforge.data.ai

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.BuildConfig
import dev.foss.obdforge.domain.ai.LocalAiConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FullLocalAiTest {
    @Test
    fun fullFlavor_keepsOptionalGemmaDownload() {
        assertFalse(BuildConfig.FDROID)
        assertTrue(BuildConfig.LOCAL_AI_ENABLED)
        assertTrue(LocalAiConfig.MODEL_DOWNLOAD_URL.contains("huggingface.co"))
        assertTrue(LocalAiConfig.MODEL_DISPLAY_NAME.contains("Gemma"))
    }

    @Test
    fun engineWithoutModel_staysUnavailable() {
        val context: Context = ApplicationProvider.getApplicationContext()
        assertFalse(LlmModelProvisioner.isModelPresent(context))
        val engine = LocalLlmEngineFactory.create(context)
        assertTrue(engine is MediaPipeLlmEngine)
        assertFalse(engine.isAvailable)
    }
}
