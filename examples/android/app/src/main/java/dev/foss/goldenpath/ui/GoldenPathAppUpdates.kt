package dev.foss.goldenpath.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.foss.goldenpath.R
import dev.foss.goldenpath.about.AppUpdatePreferences
import dev.foss.goldenpath.about.CheckSchedule
import dev.foss.goldenpath.about.ReleaseAsset
import dev.foss.goldenpath.about.ReleaseAssetSelector
import dev.foss.goldenpath.about.ReleaseTagFetcher
import dev.foss.goldenpath.about.UpdateApplyCoordinator
import dev.foss.goldenpath.about.UpdateStatusEvaluator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class GoldenPathUpdateUi(
    val updateStatus: String,
    val canApplyUpdate: Boolean,
    val onApplyUpdate: () -> Unit,
)

@Composable
fun rememberGoldenPathUpdateUi(
    context: Context,
    scope: CoroutineScope,
    activity: ComponentActivity?,
    appVersion: String,
    appUpdatePreferences: AppUpdatePreferences,
    isOnline: Boolean,
    installedFormat: String?,
    checkInterval: String,
    lastChecked: Long?,
    pendingRestart: Boolean,
): GoldenPathUpdateUi {
    var updateStatus by remember { mutableStateOf(context.getString(R.string.about_update_current)) }
    var applyAsset by remember { mutableStateOf<ReleaseAsset?>(null) }

    LaunchedEffect(pendingRestart) {
        if (pendingRestart) {
            updateStatus = context.getString(R.string.about_update_restarting)
        }
    }

    LaunchedEffect(checkInterval, lastChecked, isOnline, installedFormat, pendingRestart) {
        if (pendingRestart) return@LaunchedEffect
        if (!isOnline) return@LaunchedEffect
        if (!CheckSchedule.shouldCheck(checkInterval, lastChecked, System.currentTimeMillis())) return@LaunchedEffect
        val repo = ReleaseTagFetcher.loadReleaseRepo(context) ?: return@LaunchedEffect
        val release = ReleaseTagFetcher.fetchLatestRelease(repo) ?: return@LaunchedEffect
        val format = installedFormat ?: "apk"
        if (release.assets.isNotEmpty() && ReleaseAssetSelector.select(release.assets, format) == null) {
            updateStatus = context.getString(R.string.about_update_no_compatible)
            return@LaunchedEffect
        }
        appUpdatePreferences.setLastChecked(System.currentTimeMillis())
        val selected = ReleaseAssetSelector.select(release.assets, format)
        applyAsset = when (val result = UpdateStatusEvaluator.evaluate(appVersion, release.tag)) {
            is UpdateStatusEvaluator.Result.Current -> {
                updateStatus = context.getString(R.string.about_update_current)
                null
            }
            is UpdateStatusEvaluator.Result.Available -> {
                updateStatus = context.getString(R.string.about_update_available, result.version)
                selected
            }
        }
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
