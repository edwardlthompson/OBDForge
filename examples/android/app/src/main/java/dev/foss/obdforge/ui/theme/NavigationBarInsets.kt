package dev.foss.obdforge.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Typical minimum bottom inset for 3-button navigation vs gesture home pill. */
internal val ThreeButtonNavThreshold = 40.dp

data class NavigationBarLayout(
    val isVisible: Boolean,
    val usesThreeButtonNavigation: Boolean,
    val bottomInset: Dp,
)

fun navigationBarLayoutFromInset(bottomInset: Dp): NavigationBarLayout =
    NavigationBarLayout(
        isVisible = bottomInset > 0.dp,
        usesThreeButtonNavigation = bottomInset >= ThreeButtonNavThreshold,
        bottomInset = bottomInset,
    )

@Composable
fun rememberNavigationBarLayout(): NavigationBarLayout {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return remember(bottomInset) { navigationBarLayoutFromInset(bottomInset) }
}

/**
 * Scaffold body insets: horizontal cutout protection only.
 * Top inset is handled by TopAppBar (status bar). Bottom inset via [navigationBarGutter].
 */
val ObdScaffoldContentInsets: WindowInsets
    @Composable
    get() = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)

/** Padding above the system navigation bar (3-button, 2-button, or gesture pill). */
@Composable
fun Modifier.navigationBarGutter(): Modifier = navigationBarsPadding()

@Composable
fun navigationBarPaddingValues(): PaddingValues = WindowInsets.navigationBars.asPaddingValues()

/** Bottom padding for LazyColumn / LazyVerticalGrid content that scrolls to the screen edge. */
@Composable
fun obdScrollContentPadding(vertical: Dp = SpacingMd): PaddingValues {
    val navBottom = navigationBarPaddingValues().calculateBottomPadding()
    return PaddingValues(top = vertical, bottom = vertical + navBottom)
}

/** Full-screen root modifier clearing the system navigation bar. */
@Composable
fun Modifier.obdBottomGutter(): Modifier = navigationBarsPadding()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObdScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: (@Composable () -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = ObdScaffoldContentInsets,
        topBar = topBar,
        bottomBar = {
            if (bottomBar != null) {
                Box(Modifier.navigationBarGutter()) {
                    bottomBar()
                }
            }
        },
        floatingActionButton = floatingActionButton,
        containerColor = MaterialTheme.colorScheme.background,
        content = content,
    )
}
