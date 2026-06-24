package dev.foss.obdforge.ui.theme

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.compose.ui.unit.dp

class NavigationBarInsetsTest {

    @Test
    fun noInset_meansNoNavigationBar() {
        val layout = navigationBarLayoutFromInset(0.dp)
        assertFalse(layout.isVisible)
        assertFalse(layout.usesThreeButtonNavigation)
    }

    @Test
    fun gestureNavInset_isVisibleButNotThreeButton() {
        val layout = navigationBarLayoutFromInset(24.dp)
        assertTrue(layout.isVisible)
        assertFalse(layout.usesThreeButtonNavigation)
    }

    @Test
    fun threeButtonNavInset_isVisibleAndThreeButton() {
        val layout = navigationBarLayoutFromInset(48.dp)
        assertTrue(layout.isVisible)
        assertTrue(layout.usesThreeButtonNavigation)
    }
}
