package dev.foss.obdforge.data.ai

import dev.foss.obdforge.BuildConfig
import dev.foss.obdforge.domain.ai.LocalAiConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FullLocalAiTest {
    @Test
    fun fullFlavor_keepsOptionalGemmaDownload() {
        assertFalse(BuildConfig.FDROID)
        assertTrue(BuildConfig.LOCAL_AI_ENABLED)
        assertTrue(LocalAiConfig.MODEL_DOWNLOAD_URL.contains("huggingface.co"))
        assertTrue(LocalAiConfig.MODEL_DISPLAY_NAME.contains("Gemma"))
    }
}
