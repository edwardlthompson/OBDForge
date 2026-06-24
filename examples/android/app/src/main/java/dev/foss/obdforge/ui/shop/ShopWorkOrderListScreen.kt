package dev.foss.obdforge.ui.shop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.ui.theme.obdBottomGutter
import dev.foss.obdforge.domain.shop.WorkOrder

@Composable
fun ShopWorkOrderListScreen(
    orders: List<WorkOrder>,
    onNewIntake: () -> Unit,
    onSelectOrder: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .obdBottomGutter()
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.shop_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Button(onClick = onNewIntake, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.shop_new_intake))
        }
        if (orders.isEmpty()) {
            Text(
                text = stringResource(R.string.shop_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
                items(orders, key = { it.id }) { order ->
                    ShopWorkOrderRow(order = order, onClick = { onSelectOrder(order.id) })
                }
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.shop_back))
        }
    }
}

@Composable
private fun ShopWorkOrderRow(
    order: WorkOrder,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(SpacingMd)) {
            Text(
                text = stringResource(R.string.shop_order_row, order.customerName ?: "—", order.vin),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.shop_status_label, order.status.name),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
