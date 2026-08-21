package dev.foss.obdforge.about

import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ReleaseTagFetcherTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun loadReleaseRepoReturnsConfiguredRepo() {
        assertEquals("edwardlthompson/OBDForge", ReleaseTagFetcher.loadReleaseRepo(context))
    }

    @Test
    fun manifestDeclaresInternetPermission() {
        val info = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS,
        )
        val perms = info.requestedPermissions?.toList() ?: emptyList()
        assertTrue(
            "INTERNET permission required for GitHub release fetch",
            perms.contains("android.permission.INTERNET"),
        )
    }

    @Test
    fun parseLatestReleaseReadsAssetFilenames() {
        val parsed = ReleaseTagFetcher.parseLatestRelease(
            """
            {
              "tag_name": "v0.21.0",
              "html_url": "https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.14",
              "assets": [
                {"name": "sbom.cyclonedx.json", "browser_download_url": "https://example.com/sbom"},
                {"name": "OBDForge-1.2.14.apk", "browser_download_url": "https://example.com/a.apk"}
              ]
            }
            """.trimIndent(),
        )
        val asset = requireNotNull(parsed).assets.single { it.name.endsWith(".apk") }
        assertEquals("OBDForge-1.2.14.apk", asset.name)
        assertEquals("https://example.com/a.apk", asset.url)
        assertEquals("1.2.14", ProductUpdate.parseApkVersion(asset.name))
    }

    @Test
    fun fetchLatestReleaseReturnsNullForInvalidRepo() {
        val result = kotlinx.coroutines.runBlocking {
            ReleaseTagFetcher.fetchLatestRelease("invalid/empty-repo-404")
        }
        assertNull(result)
    }
}
