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

package cc.wordview.app.ui.screens.results

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.ui.screens.lesson.components.ReviseWord
import cc.wordview.gengolex.languages.Word
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Rule
import org.junit.Test

class ReviseResultsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = ReviseResultsViewModel

    private fun setupScreen() {
        composeTestRule.setContent {
            ProvidePreferenceLocals {
                ReviseResults(navHostController = rememberNavController())
            }
        }
    }

    @Test
    fun renders() {
        setupScreen()
        composeTestRule.onNodeWithTag("back-button").assertExists()
    }

    @Test
    fun wordsRender() {
        viewModel.setWords(
            listOf(
                ReviseWord(Word("rain", "Rain")),
                ReviseWord(Word("road", "Road")),
                ReviseWord(Word("night", "Night"))
            )
        )
        setupScreen()

        for (word in viewModel.words.value) {
            composeTestRule.onNodeWithText(word.tokenWord.word).assertExists()
        }
    }

    @Test
    fun percentageCalculatesCorrectly100() {
        viewModel.setWords(
            listOf(
                ReviseWord(Word("rain", "Rain")),
                ReviseWord(Word("road", "Road")),
                ReviseWord(Word("night", "Night"))
            )
        )
        viewModel.words.value.first().corrects++
        viewModel.words.value.last().corrects++

        setupScreen()

        composeTestRule.onNodeWithText("100% answer precision")
    }

    @Test
    fun percentageCalculatesCorrectly50() {
        viewModel.setWords(
            listOf(
                ReviseWord(Word("rain", "Rain")),
                ReviseWord(Word("road", "Road")),
                ReviseWord(Word("night", "Night"))
            )
        )
        viewModel.words.value.first().corrects++
        viewModel.words.value.last().misses++

        setupScreen()

        composeTestRule.onNodeWithText("100% answer precision")
    }
}