package dev.foss.obdforge.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.obdBottomGutter
import dev.foss.obdforge.domain.shop.WorkOrderDetail
import dev.foss.obdforge.domain.shop.WorkOrderStatus

@Composable
fun ShopWorkOrderDetailScreen(
    detail: WorkOrderDetail,
    statusMessage: String?,
    onBeginInspection: () -> Unit,
    onMarkInProgress: () -> Unit,
    onAttachSession: () -> Unit,
    onCloseOut: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val order = detail.order
    var closeNotes by remember(order.id) { mutableStateOf(order.notes.orEmpty()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .obdBottomGutter()
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.shop_detail_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(text = stringResource(R.string.shop_detail_customer, order.customerName ?: "—"))
        Text(text = stringResource(R.string.shop_detail_vin, order.vin))
        Text(text = stringResource(R.string.shop_status_label, order.status.name))
        order.sessionId?.let { sessionId ->
            Text(text = stringResource(R.string.shop_session_linked, sessionId))
        }
        when (order.status) {
            WorkOrderStatus.Intake -> Button(onClick = onBeginInspection, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.shop_begin_inspection))
            }
            WorkOrderStatus.Inspecting -> {
                Button(onClick = onAttachSession, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.shop_attach_session))
                }
                Button(onClick = onMarkInProgress, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.shop_mark_in_progress))
                }
            }
            WorkOrderStatus.InProgress -> {
                OutlinedTextField(
                    value = closeNotes,
                    onValueChange = { closeNotes = it },
                    label = { Text(stringResource(R.string.shop_close_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = { onCloseOut(closeNotes) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.shop_close_out))
                }
            }
            WorkOrderStatus.Closed -> Unit
        }
        statusMessage?.let { message ->
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.shop_back))
        }
    }
}
