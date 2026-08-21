package dev.foss.obdforge.about

/**
 * Product installer updates: compare versions from asset filenames, not git tags.
 */
object ProductUpdate {
    const val MS_DAY = 86_400_000L
    const val RELEASE_REPO = "edwardlthompson/OBDForge"
    const val RELEASES_API =
        "https://api.github.com/repos/edwardlthompson/OBDForge/releases/latest"
    const val RELEASES_PAGE =
        "https://github.com/edwardlthompson/OBDForge/releases/latest"
    const val DONATION_URL =
        "https://venmo.com/code?user_id=1857304970395648420"

    data class NamedAsset(val name: String, val url: String)
    data class ProductAsset(val version: String, val url: String)

    fun shouldCheckDaily(lastCheckAt: Long?, now: Long): Boolean {
        if (lastCheckAt == null || lastCheckAt < 0L) return true
        return now - lastCheckAt >= MS_DAY
    }

    fun coreVersion(version: String): String {
        return Regex("(\\d+\\.\\d+\\.\\d+)").find(version.trim())?.groupValues?.get(1)
            ?: version.trim()
    }

    fun isNewerVersion(current: String, latest: String): Boolean {
        return UpdateChecker.isNewer(coreVersion(current), coreVersion(latest))
    }

    fun parseApkVersion(name: String): String? {
        val match = Regex(
            "obdforge-(\\d+\\.\\d+\\.\\d+)(?:-foss)?\\.apk",
            RegexOption.IGNORE_CASE,
        ).find(name.trim())
        return match?.groupValues?.get(1)
    }

    fun selectApkAsset(assets: List<NamedAsset>): ProductAsset? {
        for (asset in assets) {
            val version = parseApkVersion(asset.name) ?: continue
            if (asset.url.isNotBlank()) return ProductAsset(version, asset.url)
        }
        return null
    }

    fun shouldNudgeDonate(lastSeenVersion: String?, currentVersion: String): Boolean {
        if (currentVersion.isBlank()) return false
        if (lastSeenVersion.isNullOrBlank()) return false
        return lastSeenVersion.trim() != currentVersion.trim()
    }

    fun shouldPromptUpdate(
        currentVersion: String,
        latestVersion: String?,
        dismissedVersion: String?,
    ): Boolean {
        if (latestVersion.isNullOrBlank()) return false
        if (!isNewerVersion(currentVersion, latestVersion)) return false
        if (dismissedVersion == latestVersion) return false
        return true
    }
}
