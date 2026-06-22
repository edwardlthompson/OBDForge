// GENERATED — do not edit; run scripts/sync-design-tokens.py
// source-hash: 12784a526aa5
package dev.foss.obdforge.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

// Raw diagnostic palette
private val GpLightDiagnosticOk = Color(0xFF2E7D32)
private val GpDarkDiagnosticOk = Color(0xFF81C784)
private val GpLightDiagnosticWarn = Color(0xFFF9A825)
private val GpDarkDiagnosticWarn = Color(0xFFFFD54F)
private val GpLightDiagnosticFault = Color(0xFFC62828)
private val GpDarkDiagnosticFault = Color(0xFFEF5350)
private val GpLightDiagnosticLive = Color(0xFF1565C0)
private val GpDarkDiagnosticLive = Color(0xFF64B5F6)
private val GpLightDiagnosticInterlock = Color(0xFF6D6D7A)
private val GpDarkDiagnosticInterlock = Color(0xFF9E9EAE)

data class DiagnosticPalette(
    val ok: Color,
    val warn: Color,
    val fault: Color,
    val live: Color,
    val interlock: Color,
)

val LightDiagnosticPalette = DiagnosticPalette(
    ok = GpLightDiagnosticOk,
    warn = GpLightDiagnosticWarn,
    fault = GpLightDiagnosticFault,
    live = GpLightDiagnosticLive,
    interlock = GpLightDiagnosticInterlock,
)

val DarkDiagnosticPalette = DiagnosticPalette(
    ok = GpDarkDiagnosticOk,
    warn = GpDarkDiagnosticWarn,
    fault = GpDarkDiagnosticFault,
    live = GpDarkDiagnosticLive,
    interlock = GpDarkDiagnosticInterlock,
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
