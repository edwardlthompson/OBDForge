package dev.foss.obdforge.about

import android.content.Context
import dev.foss.obdforge.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class LatestRelease(
    val tag: String?,
    val assets: List<ReleaseAsset>,
    val htmlUrl: String = ProductUpdate.RELEASES_PAGE,
)

object ReleaseTagFetcher {
    fun loadReleaseRepo(context: Context): String {
        return try {
            val json = context.assets.open("app-update.json").bufferedReader().use { it.readText() }
            val repo = JSONObject(json).optString("release_repo", "").trim()
            when {
                repo.isEmpty() -> ProductUpdate.RELEASE_REPO
                repo.equals("OWNER/REPO", ignoreCase = true) -> ProductUpdate.RELEASE_REPO
                else -> repo
            }
        } catch (_: Exception) {
            ProductUpdate.RELEASE_REPO
        }
    }

    fun parseLatestRelease(body: String): LatestRelease? {
        return try {
            val json = JSONObject(body)
            val tag = json.optString("tag_name", "").ifEmpty { null }
            val htmlUrl = json.optString("html_url", ProductUpdate.RELEASES_PAGE)
                .ifBlank { ProductUpdate.RELEASES_PAGE }
            val assets = mutableListOf<ReleaseAsset>()
            val arr = json.optJSONArray("assets")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val item = arr.optJSONObject(i) ?: continue
                    val name = item.optString("name", "")
                    val url = item.optString("browser_download_url", "")
                    if (name.isBlank() || url.isBlank()) continue
                    assets.add(
                        ReleaseAsset(
                            format = name.substringAfterLast('.', "bin"),
                            url = url,
                            name = name,
                        ),
                    )
                }
            }
            LatestRelease(tag, assets, htmlUrl)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun fetchLatestRelease(releaseRepo: String): LatestRelease? = withContext(Dispatchers.IO) {
        val conn = URL("https://api.github.com/repos/$releaseRepo/releases/latest")
            .openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/vnd.github+json")
            conn.setRequestProperty("User-Agent", "OBDForge/${BuildConfig.VERSION_NAME}")
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            if (conn.responseCode != HttpURLConnection.HTTP_OK) return@withContext null
            parseLatestRelease(conn.inputStream.bufferedReader().use { it.readText() })
        } catch (_: Exception) {
            null
        } finally {
            conn.disconnect()
        }
    }
}
