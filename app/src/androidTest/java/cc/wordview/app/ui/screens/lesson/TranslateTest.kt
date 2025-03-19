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

package cc.wordview.app.ui.screens.lesson

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.screens.lesson.components.ReviseWord
import cc.wordview.app.ui.screens.lesson.model.Phrase
import cc.wordview.app.ui.screens.lesson.model.TranslateViewModel
import cc.wordview.app.ui.screens.lesson.model.phraseList
import cc.wordview.gengolex.word.Word
import org.junit.Before
import org.junit.Test

class TranslateTest : ComposeTest() {
    private val viewModel = LessonViewModel
    private var translateViewModel = TranslateViewModel()

    private val word = ReviseWord(Word("sky", "céu"))
    private val phrase = Phrase("O céu é azul", listOf("O", "céu", "é", "azul"))

    @Before
    fun setup() {
        viewModel.setWord(word)
        if (phraseList.isEmpty()) phraseList.add(phrase)
        composeTestRule.setContent {
            Translate(PaddingValues(), translateViewModel)
        }
    }

    @Test
    fun renders() {
        composeTestRule.onNodeWithTag("explanation")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("O céu é azul")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("answer-area")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("word-pool")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("controls")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun fromWordPoolToAnswerArea() {
        viewModel.cleanWords()

        composeTestRule.onNodeWithTag("céu-wordpool")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("céu").performClick()

        composeTestRule.onNodeWithTag("céu-wordpool")
            .assertDoesNotExist()
    }

    @Test
    fun fromWordPoolToAnswerAreaToWordPool() {
        viewModel.cleanWords()

        composeTestRule.onNodeWithTag("céu-wordpool")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("céu").performClick()

        composeTestRule.onNodeWithTag("céu-wordpool")
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag("céu-answer")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("céu").performClick()

        composeTestRule.onNodeWithTag("céu-answer")
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag("céu-wordpool")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun checkButtonIsDisabledUntilNoWordsInWordPool() {
        composeTestRule.onNodeWithText("Check")
            .assertHasClickAction()
            .assertIsNotEnabled()

        composeTestRule.onNodeWithText("O").performClick()
        composeTestRule.onNodeWithText("Check")
            .assertHasClickAction()
            .assertIsNotEnabled()

        composeTestRule.onNodeWithText("céu").performClick()
        composeTestRule.onNodeWithText("Check")
            .assertHasClickAction()
            .assertIsNotEnabled()

        composeTestRule.onNodeWithText("é").performClick()
        composeTestRule.onNodeWithText("Check")
            .assertHasClickAction()
            .assertIsNotEnabled()

        composeTestRule.onNodeWithText("azul").performClick()
        composeTestRule.onNodeWithText("Check")
            .assertHasClickAction()
            .assertIsEnabled()

        // asserts that it works in both ways
        composeTestRule.onNodeWithText("azul").performClick()
        composeTestRule.onNodeWithText("Check")
            .assertHasClickAction()
            .assertIsNotEnabled()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkCorrectAnswer() {
        composeTestRule.onNodeWithText("Check")
            .assertHasClickAction()
            .assertIsNotEnabled()

        composeTestRule.onNodeWithText("O").performClick()
        composeTestRule.onNodeWithText("céu").performClick()
        composeTestRule.onNodeWithText("é").performClick()
        composeTestRule.onNodeWithText("azul").performClick()

        composeTestRule.onNodeWithText("Check").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasText("Correct!"), 2_000)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkWrongAnswer() {
        composeTestRule.onNodeWithText("Check")
            .assertHasClickAction()
            .assertIsNotEnabled()

        composeTestRule.onNodeWithText("O").performClick()
        composeTestRule.onNodeWithText("céu").performClick()
        composeTestRule.onNodeWithText("azul").performClick()
        composeTestRule.onNodeWithText("é").performClick()

        composeTestRule.onNodeWithText("Check").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasText("Wrong!"), 2_000)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkCorrectAnswerLockWords() {
        composeTestRule.onNodeWithText("Check")
            .assertHasClickAction()
            .assertIsNotEnabled()

        composeTestRule.onNodeWithText("O").performClick()
        composeTestRule.onNodeWithText("céu").performClick()
        composeTestRule.onNodeWithText("é").performClick()
        composeTestRule.onNodeWithText("azul").performClick()

        composeTestRule.onNodeWithText("Check").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasText("Correct!"), 2_000)

        composeTestRule.onNodeWithText("céu").performClick()
        composeTestRule.onNodeWithTag("céu-wordpool")
            .assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkWrongAnswerLockWords() {
        composeTestRule.onNodeWithText("Check")
            .assertHasClickAction()
            .assertIsNotEnabled()

        composeTestRule.onNodeWithText("O").performClick()
        composeTestRule.onNodeWithText("céu").performClick()
        composeTestRule.onNodeWithText("azul").performClick()
        composeTestRule.onNodeWithText("é").performClick()

        composeTestRule.onNodeWithText("Check").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasText("Wrong!"), 2_000)

        composeTestRule.onNodeWithTag("céu-answer").performClick()
        composeTestRule.onNodeWithTag("céu-wordpool")
            .assertDoesNotExist()
    }
}