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

package cc.wordview.app.home.revise

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.ui.screens.revise.Translate
import cc.wordview.app.ui.screens.revise.model.TranslateViewModel
import cc.wordview.app.ui.theme.WordViewTheme
import cc.wordview.gengolex.languages.Word
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Rule
import org.junit.Test

class TranslateTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = TranslateViewModel

    private fun setupScreen(autoAdvance: Boolean = true) {
        composeTestRule.mainClock.autoAdvance = autoAdvance
        composeTestRule.setContent {
            WordViewTheme {
                ProvidePreferenceLocals {
                    Translate(innerPadding = PaddingValues())
                }
            }
        }
    }

    @Test
    fun renders() {
        viewModel.setPhrase("He is standing on the hill.")

        viewModel.appendWords(
            arrayListOf(
                Word("he", "彼"),
                Word("is", "は"),
                Word("hill", "丘"),
                Word("in", "の"),
                Word("top", "上"),
                Word("is", "に"),
                Word("standing", "立っている"),
            )
        )

        setupScreen()
        composeTestRule.onNodeWithTag("explanation").assertExists()
        composeTestRule.onNodeWithTag("answer-area").assertExists()
        composeTestRule.onNodeWithTag("word-pool").assertExists()
        composeTestRule.onNodeWithText("He is standing on the hill.").assertExists()
    }

    @Test
    fun wordPoolWorks() {
        val words = arrayListOf(
            Word("he", "彼"),
            Word("is", "は"),
            Word("hill", "丘"),
            Word("in", "の"),
            Word("top", "上"),
            Word("is", "に"),
            Word("standing", "立っている"),
        )

        viewModel.setPhrase("He is standing on the hill.")
        viewModel.appendWords(words)

        setupScreen()

        for (word in words) {
            composeTestRule.onNodeWithText(word.word).assertExists()
            composeTestRule.onNodeWithText(word.word).performClick()
            assert(viewModel.wordPool.indexOf(word) == -1)
        }

        for (word in words) {
            composeTestRule.onNodeWithText(word.word).assertExists()
            composeTestRule.onNodeWithText(word.word).performClick()
            assert(viewModel.answerWordPool.indexOf(word) == -1)
        }
    }
}