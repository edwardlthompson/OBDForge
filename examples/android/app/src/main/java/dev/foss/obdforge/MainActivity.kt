package dev.foss.obdforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import dev.foss.obdforge.about.AppUpdatePreferences
import dev.foss.obdforge.network.NetworkStatusMonitor
import dev.foss.obdforge.ui.shell.GoldenPathApp
import dev.foss.obdforge.ui.theme.ThemePreferences
import dev.foss.obdforge.BuildConfig
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.diagnostics.CrashLogHandler
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventCategory
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventSeverity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var networkStatusMonitor: NetworkStatusMonitor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val themePreferences = ThemePreferences(applicationContext)
        val appUpdatePreferences = AppUpdatePreferences(applicationContext)
        networkStatusMonitor = NetworkStatusMonitor(applicationContext).also { it.start() }

        lifecycleScope.launch {
            appUpdatePreferences.clearPendingRestart()
            appUpdatePreferences.ensureInstalledFormat()
        }

        val compositionRoot = ObdForgeCompositionRoot.create(applicationContext, lifecycleScope)
        CrashLogHandler.install(applicationContext, compositionRoot.diagnosticEventRepository, lifecycleScope)
        lifecycleScope.launch {
            compositionRoot.diagnosticEventRecorder.recordNow(
                category = DiagnosticEventCategory.App,
                severity = DiagnosticEventSeverity.Info,
                message = "App started (${BuildConfig.VERSION_NAME})",
            )
        }

        setContent {
            GoldenPathApp(
                context = this,
                scope = lifecycleScope,
                themePreferences = themePreferences,
                appUpdatePreferences = appUpdatePreferences,
                networkStatusMonitor = networkStatusMonitor!!,
                compositionRoot = compositionRoot,
            )
        }
    }

    override fun onDestroy() {
        networkStatusMonitor?.stop()
        super.onDestroy()
    }
}
