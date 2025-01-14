/*
 * Copyright (c) 2025 Arthur Araujo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package cc.wordview.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import cc.wordview.app.hasAlpha
import cc.wordview.app.ui.components.FadeOutBox
import cc.wordview.app.ui.theme.WordViewTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FadeOutBoxTest {
    @get:Rule
    val composeTestRule = createComposeRule()

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
