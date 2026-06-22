package dev.foss.obdforge.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

/** OBDForge diagnostic semantic colors (Garage theme extensions). */
data class DiagnosticPalette(
    val ok: Color,
    val warn: Color,
    val fault: Color,
    val live: Color,
    val interlock: Color,
)

val LightDiagnosticPalette = DiagnosticPalette(
    ok = Color(0xFF2E7D32),
    warn = Color(0xFFF9A825),
    fault = Color(0xFFC62828),
    live = Color(0xFF1565C0),
    interlock = Color(0xFF6D6D7A),
)

val DarkDiagnosticPalette = DiagnosticPalette(
    ok = Color(0xFF81C784),
    warn = Color(0xFFFFD54F),
    fault = Color(0xFFEF5350),
    live = Color(0xFF64B5F6),
    interlock = Color(0xFF9E9EAE),
)

val ColorScheme.diagnosticOk: Color
    get() = if (this.background.luminance() > 0.5f) LightDiagnosticPalette.ok else DarkDiagnosticPalette.ok

val ColorScheme.diagnosticWarn: Color
    get() = if (this.background.luminance() > 0.5f) LightDiagnosticPalette.warn else DarkDiagnosticPalette.warn

val ColorScheme.diagnosticFault: Color
    get() = if (this.background.luminance() > 0.5f) LightDiagnosticPalette.fault else DarkDiagnosticPalette.fault

val ColorScheme.diagnosticLive: Color
    get() = if (this.background.luminance() > 0.5f) LightDiagnosticPalette.live else DarkDiagnosticPalette.live

val ColorScheme.diagnosticInterlock: Color
    get() = if (this.background.luminance() > 0.5f) LightDiagnosticPalette.interlock else DarkDiagnosticPalette.interlock

private fun Color.luminance(): Float =
    0.299f * red + 0.587f * green + 0.114f * blue
