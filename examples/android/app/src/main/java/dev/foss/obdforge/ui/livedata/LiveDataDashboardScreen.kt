package dev.foss.obdforge.ui.livedata

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R
import dev.foss.goldenpath.ui.theme.SpacingMd
import dev.foss.goldenpath.ui.theme.SpacingSm
import dev.foss.obdforge.domain.livedata.LiveDataLayoutConfig
import dev.foss.obdforge.domain.livedata.LiveDataLayoutResolver
import dev.foss.obdforge.domain.livedata.LiveDataSnapshot
import dev.foss.obdforge.domain.livedata.LivePidSample
import dev.foss.obdforge.domain.livedata.PersonaMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDataDashboardScreen(
    snapshot: LiveDataSnapshot,
    persona: PersonaMode,
    paused: Boolean,
    onPauseToggle: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val layout = LiveDataLayoutResolver.resolve(persona)
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.livedata_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.livedata_back),
                        )
                    }
                },
                actions = {
                    LiveDataPauseAction(paused = paused, onPauseToggle = onPauseToggle)
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            LiveDataPersonaLabel(persona = persona)
            LiveDataPidGrid(
                layout = layout,
                snapshot = snapshot,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun LiveDataPersonaLabel(persona: PersonaMode) {
    Text(
        text = stringResource(
            R.string.livedata_persona_active,
            stringResource(personaLabelRes(persona)),
        ),
        style = MaterialTheme.typography.labelLarge,
    )
}

private fun personaLabelRes(mode: PersonaMode): Int = when (mode) {
    PersonaMode.Diy -> R.string.persona_diy
    PersonaMode.SemiPro -> R.string.persona_semi_pro
    PersonaMode.Shop -> R.string.persona_shop
    PersonaMode.Racing -> R.string.persona_racing
}

@Composable
private fun LiveDataPidGrid(
    layout: LiveDataLayoutConfig,
    snapshot: LiveDataSnapshot,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(layout.columns),
        modifier = modifier,
        contentPadding = PaddingValues(bottom = SpacingMd),
        horizontalArrangement = Arrangement.spacedBy(SpacingSm),
        verticalArrangement = Arrangement.spacedBy(SpacingSm),
    ) {
        items(layout.pids) { pid ->
            val sample = snapshot.samples[pid]
            if (layout.compact) {
                PidCompactCell(sample = sample, pid = pid)
            } else {
                PidValueCard(sample = sample, pid = pid)
            }
        }
    }
}

@Composable
private fun LiveDataPauseAction(
    paused: Boolean,
    onPauseToggle: () -> Unit,
) {
    FilterChip(
        selected = paused,
        onClick = onPauseToggle,
        label = {
            Text(
                stringResource(
                    if (paused) R.string.livedata_resume else R.string.livedata_pause,
                ),
            )
        },
    )
}

@Composable
private fun PidValueCard(
    sample: LivePidSample?,
    pid: Int,
) {
    PidValueCardContent(
        title = sample?.name ?: PidCatalogFallback.nameFor(pid),
        value = sample?.formattedValue ?: "—",
        compact = false,
    )
}

@Composable
private fun PidCompactCell(
    sample: LivePidSample?,
    pid: Int,
) {
    PidValueCardContent(
        title = sample?.name ?: PidCatalogFallback.nameFor(pid),
        value = sample?.formattedValue ?: "—",
        compact = true,
    )
}

private object PidCatalogFallback {
    fun nameFor(pid: Int): String = "PID %02X".format(pid)
}
