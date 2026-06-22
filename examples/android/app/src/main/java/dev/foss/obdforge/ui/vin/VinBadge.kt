package dev.foss.obdforge.ui.vin

import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.domain.vehicle.VinSourceType

@Composable
fun VinBadge(
    source: VinSourceType,
    modifier: Modifier = Modifier,
) {
    AssistChip(
        onClick = {},
        modifier = modifier,
        enabled = false,
        label = {
            Text(
                text = stringResource(sourceLabelRes(source)),
                style = MaterialTheme.typography.labelSmall,
            )
        },
    )
}

private fun sourceLabelRes(source: VinSourceType): Int = when (source) {
    VinSourceType.EcuObd2, VinSourceType.EcuUds, VinSourceType.EcuKwp, VinSourceType.EcuJ1939 ->
        R.string.vin_source_badge_ecu
    VinSourceType.Barcode -> R.string.vin_source_badge_barcode
    VinSourceType.Manual -> R.string.vin_source_badge_manual
    VinSourceType.Demo -> R.string.vin_source_badge_demo
    VinSourceType.PlateLookup -> R.string.vin_source_badge_manual
}
