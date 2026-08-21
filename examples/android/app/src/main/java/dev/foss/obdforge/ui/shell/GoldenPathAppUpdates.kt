package dev.foss.obdforge.ui.shell

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.foss.obdforge.R
import dev.foss.obdforge.about.AppUpdatePreferences
import dev.foss.obdforge.about.ProductUpdate
import dev.foss.obdforge.about.ProductUpdatePrefs
import dev.foss.obdforge.about.ReleaseAsset
import dev.foss.obdforge.about.ReleaseTagFetcher
import dev.foss.obdforge.about.UpdateApplyCoordinator
import dev.foss.obdforge.settings.SettingsLogic
import dev.foss.obdforge.ui.about.DonateNudgeDialog
import dev.foss.obdforge.ui.about.UpdateAvailableDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class GoldenPathUpdateUi(
    val updateStatus: String,
    val canApplyUpdate: Boolean,
    val onApplyUpdate: () -> Unit,
)

private data class PendingUpdate(val version: String, val url: String)

@Composable
fun rememberGoldenPathUpdateUi(
    context: Context,
    scope: CoroutineScope,
    activity: ComponentActivity?,
    appVersion: String,
    appUpdatePreferences: AppUpdatePreferences,
    isOnline: Boolean,
    checkInterval: String,
    pendingRestart: Boolean,
    promptsEnabled: Boolean,
): GoldenPathUpdateUi {
    var updateStatus by remember { mutableStateOf(context.getString(R.string.about_update_current)) }
    var applyAsset by remember { mutableStateOf<ReleaseAsset?>(null) }
    var showDonate by remember { mutableStateOf(false) }
    var pendingUpdate by remember { mutableStateOf<PendingUpdate?>(null) }
    val prefs = remember { ProductUpdatePrefs(context) }

    LaunchedEffect(pendingRestart) {
        if (pendingRestart) {
            updateStatus = context.getString(R.string.about_update_restarting)
        }
    }

    LaunchedEffect(promptsEnabled, isOnline, checkInterval, pendingRestart) {
        if (!promptsEnabled || pendingRestart) return@LaunchedEffect
        if (ProductUpdate.shouldNudgeDonate(prefs.lastSeenVersion(), appVersion)) {
            showDonate = true
            return@LaunchedEffect
        }
        prefs.markVersionSeen(appVersion)
        if (!isOnline) return@LaunchedEffect
        if (!SettingsLogic.isUpdateCheckEnabled(checkInterval)) return@LaunchedEffect
        if (!ProductUpdate.shouldCheckDaily(prefs.lastCheckAt(), System.currentTimeMillis())) {
            return@LaunchedEffect
        }
        val repo = ReleaseTagFetcher.loadReleaseRepo(context)
        val release = ReleaseTagFetcher.fetchLatestRelease(repo)
        prefs.markChecked(System.currentTimeMillis())
        if (release == null || release.assets.isEmpty()) return@LaunchedEffect
        val named = release.assets.map { ProductUpdate.NamedAsset(it.name, it.url) }
        val selected = ProductUpdate.selectApkAsset(named) ?: return@LaunchedEffect
        if (!ProductUpdate.shouldPromptUpdate(appVersion, selected.version, prefs.dismissedVersion())) {
            return@LaunchedEffect
        }
        val asset = ReleaseAsset(format = "apk", url = selected.url, name = "OBDForge-${selected.version}.apk")
        applyAsset = asset
        updateStatus = context.getString(R.string.about_update_available, selected.version)
        pendingUpdate = PendingUpdate(
            version = selected.version,
            url = selected.url.ifBlank { release.htmlUrl.ifBlank { ProductUpdate.RELEASES_PAGE } },
        )
    }

    if (showDonate) {
        DonateNudgeDialog(
            onDonate = {
                prefs.markVersionSeen(appVersion)
                showDonate = false
                openExternalUrl(context, ProductUpdate.DONATION_URL)
            },
            onNotNow = {
                prefs.markVersionSeen(appVersion)
                showDonate = false
            },
        )
    }
    pendingUpdate?.let { prompt ->
        UpdateAvailableDialog(
            version = prompt.version,
            onInstall = {
                prefs.markChecked(System.currentTimeMillis(), prompt.version)
                pendingUpdate = null
                openExternalUrl(context, prompt.url.ifBlank { ProductUpdate.RELEASES_PAGE })
            },
            onLater = {
                prefs.markChecked(System.currentTimeMillis(), prompt.version)
                pendingUpdate = null
            },
        )
    }

    return GoldenPathUpdateUi(
        updateStatus = updateStatus,
        canApplyUpdate = applyAsset != null,
        onApplyUpdate = {
            val asset = applyAsset ?: return@GoldenPathUpdateUi
            val host = activity ?: return@GoldenPathUpdateUi
            scope.launch {
                UpdateApplyCoordinator.applySideloadUpdate(host, appUpdatePreferences, asset)
            }
        },
    )
}

private fun openExternalUrl(context: Context, url: String) {
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    } catch (_: Exception) {
        // Stay silent if no browser is available.
    }
}
