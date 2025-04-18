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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.components.TranslateResultContainer
import org.junit.Test

class TranslateResultContainerTest : ComposeTest() {
    private fun setup(correct: Boolean, words: List<String>) {
        composeTestRule.setContent {
            TranslateResultContainer(correct, words)
        }
    }

    @Test
    fun correct() {
        setup(true, listOf())

        composeTestRule.onNodeWithText("Correct!")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("You answered correctly! Click proceed to continue the lesson.")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Correct icon")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun wrong() {
        val words = listOf("Hello", "World")
        setup(false, words)

        composeTestRule.onNodeWithText("Wrong!")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("You wrongly translated the phrase! The correct order is:")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Incorrect icon")
            .assertExists()
            .assertIsDisplayed()

        for (word in words) {
            composeTestRule.onNodeWithText(word).assertExists().assertIsDisplayed()
        }
    }
}