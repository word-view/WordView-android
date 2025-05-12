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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import cc.wordview.app.ui.activities.lesson.LessonActivity
import cc.wordview.app.ui.activities.lesson.LessonNav
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.activities.lesson.viewmodel.ReviseWord
import cc.wordview.gengolex.word.Word
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LessonTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<LessonActivity>()

    private val viewModel = LessonViewModel.apply {
        appendWord(ReviseWord(Word("tear", "lágrima", representable = true)))
        appendWord(ReviseWord(Word("rain", "chuva", representable = true)))
        appendWord(ReviseWord(Word("umbrella", "guarda-chuva", representable = true)))
    }

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun renders() {
        composeTestRule.onNodeWithTag("back-button")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("root")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("top-word")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("drag")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("down-word")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun backOpensConfirmation() {
        composeTestRule.onNodeWithTag("back-button")
            .performClick()

        composeTestRule.onNodeWithTag("lesson-quit-alert-dialog")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun leave() {
        composeTestRule.onNodeWithTag("back-button")
            .performClick()

        composeTestRule.onNodeWithText("Finish")
            .performClick()
    }

    @Test
    fun performLesson() {
        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, 500f))
            up()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun timerWorks() {
        composeTestRule.waitUntilAtLeastOneExists(hasText("4:59"), 5_000)
    }
}