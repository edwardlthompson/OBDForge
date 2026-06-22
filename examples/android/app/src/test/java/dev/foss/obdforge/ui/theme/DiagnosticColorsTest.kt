package dev.foss.obdforge.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import org.junit.Assert.assertEquals
import org.junit.Test

class DiagnosticColorsTest {
    @Test
    fun lightAndDarkPalettes_differForFault() {
        val light: ColorScheme = lightColorScheme()
        val dark: ColorScheme = darkColorScheme()
        assertEquals(LightDiagnosticPalette.fault, light.diagnosticFault)
        assertEquals(DarkDiagnosticPalette.fault, dark.diagnosticFault)
    }
}
