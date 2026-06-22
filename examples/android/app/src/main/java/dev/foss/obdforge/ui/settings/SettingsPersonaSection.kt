package dev.foss.obdforge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.obdforge.R
import dev.foss.obdforge.ui.theme.SpacingMd
import dev.foss.obdforge.domain.livedata.PersonaMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsPersonaSection(
    persona: PersonaMode,
    onPersonaSelect: (PersonaMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.settings_persona_label),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.settings_persona_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            PersonaMode.entries.forEach { mode ->
                FilterChip(
                    selected = persona == mode,
                    onClick = { onPersonaSelect(mode) },
                    label = { Text(stringResource(personaLabelRes(mode))) },
                )
            }
        }
    }
}

private fun personaLabelRes(mode: PersonaMode): Int = when (mode) {
    PersonaMode.Diy -> R.string.persona_diy
    PersonaMode.SemiPro -> R.string.persona_semi_pro
    PersonaMode.Shop -> R.string.persona_shop
    PersonaMode.Racing -> R.string.persona_racing
}
