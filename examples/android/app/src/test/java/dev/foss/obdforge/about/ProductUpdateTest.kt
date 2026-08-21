package dev.foss.obdforge.about

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductUpdateTest {

    @Test
    fun dailyCheckWaitsAFullDay() {
        assertTrue(ProductUpdate.shouldCheckDaily(null, 0L))
        assertFalse(ProductUpdate.shouldCheckDaily(0L, ProductUpdate.MS_DAY - 1))
        assertTrue(ProductUpdate.shouldCheckDaily(0L, ProductUpdate.MS_DAY))
    }

    @Test
    fun apkVersionIgnoresTemplateTags() {
        assertEquals("1.2.13", ProductUpdate.parseApkVersion("OBDForge-1.2.13.apk"))
        assertEquals("1.2.14", ProductUpdate.parseApkVersion("obdforge-1.2.14-foss.apk"))
        assertEquals(null, ProductUpdate.parseApkVersion("v0.21.0"))
        assertEquals(null, ProductUpdate.parseApkVersion("v1.2.13"))
    }

    @Test
    fun donateNudgeOnlyAfterVersionChange() {
        assertFalse(ProductUpdate.shouldNudgeDonate(null, "1.2.13"))
        assertFalse(ProductUpdate.shouldNudgeDonate("1.2.13", "1.2.13"))
        assertTrue(ProductUpdate.shouldNudgeDonate("1.2.13", "1.2.14"))
    }

    @Test
    fun selectApkAssetReadsInstallerFilename() {
        val picked = ProductUpdate.selectApkAsset(
            listOf(
                ProductUpdate.NamedAsset("sbom.cyclonedx.json", "https://example.com/sbom"),
                ProductUpdate.NamedAsset("OBDForge-1.2.14.apk", "https://example.com/a.apk"),
            ),
        )
        assertEquals("1.2.14", picked?.version)
        assertEquals("https://example.com/a.apk", picked?.url)
    }

    @Test
    fun newerThanCurrentUsesSemver() {
        assertTrue(ProductUpdate.isNewerVersion("1.2.13", "1.2.14"))
        assertTrue(ProductUpdate.isNewerVersion("1.2.13-debug", "1.2.14"))
        assertFalse(ProductUpdate.isNewerVersion("1.2.14", "1.2.13"))
        assertFalse(ProductUpdate.isNewerVersion("1.2.14", "1.2.14"))
    }

    @Test
    fun updatePromptSkipsDismissedVersion() {
        assertTrue(ProductUpdate.shouldPromptUpdate("1.2.13", "1.2.14", null))
        assertFalse(ProductUpdate.shouldPromptUpdate("1.2.13", "1.2.14", "1.2.14"))
        assertFalse(ProductUpdate.shouldPromptUpdate("1.2.14", "1.2.14", null))
    }
}
