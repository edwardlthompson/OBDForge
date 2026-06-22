package dev.foss.obdforge.ui.vin

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.obdforge.R
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.TransportSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun VinResolveHost(
    root: ObdForgeCompositionRoot,
    scope: CoroutineScope,
    demoModeEnabled: Boolean,
    transportSelection: TransportSelection,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coordinator = androidx.compose.runtime.remember(root, demoModeEnabled, transportSelection) {
        VinResolveCoordinator(
            resolveVinUseCase = root.resolveVinUseCase,
            vinProfileRepository = root.vinProfileRepository,
            transportRegistry = root.transportRegistry,
            transportSelection = transportSelection,
            demoModeEnabled = demoModeEnabled,
        )
    }
    val latestProfile by coordinator.latestProfile.collectAsStateWithLifecycle(initialValue = null)
    val statusKey by coordinator.statusMessage.collectAsStateWithLifecycle(initialValue = null)
    val showCamera by coordinator.showCamera.collectAsStateWithLifecycle(initialValue = false)

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) coordinator.openScanner()
    }

    LaunchedEffect(coordinator) {
        coordinator.refreshProfile()
    }

    val statusMessage = statusKey?.let { key ->
        when (key) {
            VinResolveCoordinator.STATUS_ERROR_LENGTH ->
                stringResource(R.string.vin_error_length)
            VinResolveCoordinator.STATUS_ERROR_CHARSET ->
                stringResource(R.string.vin_error_charset)
            VinResolveCoordinator.STATUS_ERROR_CHECK_DIGIT ->
                stringResource(R.string.vin_error_check_digit)
            VinResolveCoordinator.STATUS_ECU_UNAVAILABLE ->
                stringResource(R.string.vin_ecu_unavailable)
            VinResolveCoordinator.STATUS_TRANSPORT_UNAVAILABLE ->
                stringResource(R.string.transport_status_invalid)
            else -> key
        }
    }

    VinResolveScreen(
        latestProfile = latestProfile,
        ecuResolveEnabled = coordinator.ecuResolveEnabled,
        statusMessage = statusMessage,
        showCamera = showCamera,
        onManualSubmit = { vin -> scope.launch { coordinator.saveManual(vin) } },
        onResolveFromEcu = { scope.launch { coordinator.resolveFromEcu() } },
        onOpenScanner = {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                coordinator.openScanner()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onCloseScanner = coordinator::closeScanner,
        onBarcodeScanned = { vin -> scope.launch { coordinator.saveBarcode(vin) } },
        onBack = onBack,
        modifier = modifier,
    )
}
