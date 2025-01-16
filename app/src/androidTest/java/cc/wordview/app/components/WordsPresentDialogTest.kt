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

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.ui.components.WordsPresentDialog
import cc.wordview.gengolex.languages.Word
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class WordsPresentDialogTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val onConfirm = mock(Runnable::class.java)

    @Before
    fun setup() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            WordsPresentDialog(
                onConfirm = { onConfirm.run() },
                listOf(Word("rain", "雨"), Word("car", "車"))
            )
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun confirm() {
        composeTestRule.mainClock.autoAdvance = true
        composeTestRule.waitUntilAtLeastOneExists(hasText("雨"), 5_000)
        composeTestRule.onNodeWithText("Proceed").performClick()
        verify(onConfirm,times(1)).run()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun wordsRender() {
        composeTestRule.mainClock.autoAdvance = true
        composeTestRule.waitUntilAtLeastOneExists(hasText("雨"), 5_000)
        composeTestRule.waitUntilAtLeastOneExists(hasText("rain"), 5_000)
        composeTestRule.waitUntilAtLeastOneExists(hasText("車"), 5_000)
        composeTestRule.waitUntilAtLeastOneExists(hasText("car"), 5_000)
    }
}