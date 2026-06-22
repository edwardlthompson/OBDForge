package dev.foss.obdforge.ui.shop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.goldenpath.R
import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.domain.vehicle.VehicleProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private enum class ShopScreen {
    List,
    Intake,
    Detail,
}

@Composable
fun ShopHost(
    root: ObdForgeCompositionRoot,
    scope: CoroutineScope,
    savedVehicleProfile: VehicleProfile?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coordinator = remember(root) { ShopCoordinator(root.shopRepository) }
    val openOrders by coordinator.openOrders.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedDetail by coordinator.selectedDetail.collectAsStateWithLifecycle(initialValue = null)
    val statusKey by coordinator.statusMessage.collectAsStateWithLifecycle(initialValue = null)
    var screen by remember { mutableStateOf(ShopScreen.List) }

    val statusMessage = statusKey?.let { key ->
        when (key) {
            ShopCoordinator.STATUS_INVALID_INTAKE ->
                stringResource(R.string.shop_error_invalid_intake)
            ShopCoordinator.STATUS_NO_SESSION ->
                stringResource(R.string.shop_error_no_session)
            else -> key
        }
    }

    when (screen) {
        ShopScreen.List -> ShopWorkOrderListScreen(
            orders = openOrders,
            onNewIntake = {
                coordinator.clearSelection()
                screen = ShopScreen.Intake
            },
            onSelectOrder = { orderId ->
                scope.launch {
                    coordinator.selectOrder(orderId)
                    screen = ShopScreen.Detail
                }
            },
            onBack = {
                coordinator.clearSelection()
                onBack()
            },
            modifier = modifier,
        )
        ShopScreen.Intake -> ShopIntakeScreen(
            defaultVin = savedVehicleProfile?.vin.orEmpty(),
            statusMessage = statusMessage,
            onSubmit = { name, phone, vin, notes ->
                scope.launch {
                    val order = coordinator.createIntake(name, phone, vin, notes)
                    if (order != null) {
                        coordinator.selectOrder(order.id)
                        screen = ShopScreen.Detail
                    }
                }
            },
            onBack = { screen = ShopScreen.List },
            modifier = modifier,
        )
        ShopScreen.Detail -> selectedDetail?.let { detail ->
            ShopWorkOrderDetailScreen(
                detail = detail,
                statusMessage = statusMessage,
                onBeginInspection = {
                    scope.launch { coordinator.beginInspection(detail.order.id) }
                },
                onMarkInProgress = {
                    scope.launch { coordinator.markInProgress(detail.order.id) }
                },
                onAttachSession = {
                    scope.launch { coordinator.attachLatestSession(detail.order.id) }
                },
                onCloseOut = { notes ->
                    scope.launch {
                        coordinator.closeOut(detail.order.id, notes)
                        screen = ShopScreen.List
                    }
                },
                onBack = {
                    coordinator.clearSelection()
                    screen = ShopScreen.List
                },
                modifier = modifier,
            )
        }
    }
}
