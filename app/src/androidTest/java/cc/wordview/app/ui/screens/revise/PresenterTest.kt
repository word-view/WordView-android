/*
 * Copyright (c) 2024 Arthur Araujo
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

package cc.wordview.app.ui.screens.revise

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import cc.wordview.app.ui.screens.revise.components.Answer
import cc.wordview.app.ui.screens.revise.components.ReviseWord
import cc.wordview.app.ui.theme.WordViewTheme
import cc.wordview.gengolex.languages.Word
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Rule
import org.junit.Test

class PresenterTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = WordReviseViewModel

    private fun setupScreen(autoAdvance: Boolean = true) {
        composeTestRule.mainClock.autoAdvance = autoAdvance
        composeTestRule.setContent {
            WordViewTheme {
                ProvidePreferenceLocals {
                    Presenter()
                }
            }
        }
    }

    @Test
    fun renders() {
        setupScreen()
        composeTestRule.onNodeWithTag("root").assertExists()
    }

    @Test
    fun answerCorrect() {
        viewModel.setWord(ReviseWord(Word("rain", "chuva")))
        viewModel.setAnswer(Answer.CORRECT)
        setupScreen(false)
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.onNodeWithTag("correct").assertExists()
        composeTestRule.mainClock.advanceTimeBy(2000)
        composeTestRule.onNodeWithTag("word").assertExists()
        composeTestRule.onNodeWithText("chuva").assertExists()
    }

    @Test
    fun answerWrong() {
        viewModel.setWord(ReviseWord(Word("rain", "chuva")))
        viewModel.setAnswer(Answer.WRONG)
        setupScreen(false)
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.onNodeWithTag("wrong").assertExists()
        composeTestRule.mainClock.advanceTimeBy(2000)
        composeTestRule.onNodeWithTag("word").assertExists()
        composeTestRule.onNodeWithText("chuva").assertExists()
    }
}