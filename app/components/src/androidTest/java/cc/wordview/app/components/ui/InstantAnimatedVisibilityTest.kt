package cc.wordview.app.components.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import org.junit.Test

class InstantAnimatedVisibilityTest : ComposeTest() {
    @Test
    fun contentIsVisibleAfterAnimation() {
        composeTestRule.setContent {
            InstantAnimatedVisibility {
                Text("Hello World!")
            }
        }
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.mainClock.advanceTimeBy(500L)
        composeTestRule.onNodeWithText("Hello World!")
            .assertExists()
            .assertIsDisplayed()
    }
}