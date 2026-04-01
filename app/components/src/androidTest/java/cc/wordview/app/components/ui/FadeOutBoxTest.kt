package cc.wordview.app.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import cc.wordview.app.components.hasAlpha
import org.junit.Before
import org.junit.Test

class FadeOutBoxTest : ComposeTest() {
    @Before
    fun setup() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            FadeOutBox(
                duration = 1000,
                stagnationTime = 5000
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary))
            }
        }
    }

    @Test
    fun initiallyVisible() {
        composeTestRule.onNodeWithTag("fade-box")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun fadesOutAfterStagnationTime() {
        composeTestRule.mainClock.advanceTimeBy(250)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("fade-box")
            .assertExists()
            .assert(hasAlpha(1f))

        composeTestRule.mainClock.advanceTimeBy(100)
        composeTestRule.mainClock.advanceTimeBy(6000)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("fade-box")
            .assertExists()
            .assert(hasAlpha(0f))
    }

    @Test
    fun toggleVisibilityOnClick() {
        composeTestRule.mainClock.autoAdvance = true
        val fadeOutBox = composeTestRule.onNodeWithTag("fade-box")

        fadeOutBox.assert(hasAlpha(1f))

        fadeOutBox.performClick()
        composeTestRule.waitForIdle()
        fadeOutBox.assert(hasAlpha(0f))

        fadeOutBox.performClick()
        composeTestRule.waitForIdle()
        fadeOutBox.assert(hasAlpha(1f))
    }
}
