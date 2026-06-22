package dev.foss.obdforge

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class GoldenPathUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun dismissWelcomeIfShown() {
        composeTestRule.waitForIdle()
        val continueNodes = composeTestRule.onAllNodesWithText("Continue to app")
            .fetchSemanticsNodes(atLeastOneRootExists = false)
        if (continueNodes.isNotEmpty()) {
            composeTestRule.onNodeWithText("Continue to app").performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun opensSettingsPanelWithThemeAndUpdateControls() {
        dismissWelcomeIfShown()
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Check for updates").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark theme").performClick()
        composeTestRule.onNodeWithText("Close settings").performClick()
    }

    @Test
    fun opensAboutPanelWithVersion() {
        dismissWelcomeIfShown()
        composeTestRule.onNodeWithContentDescription("About").performClick()
        composeTestRule.onNodeWithText("About").assertIsDisplayed()
        composeTestRule.onNodeWithText("Installed format: apk").assertIsDisplayed()
    }
}
