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

package cc.wordview.app.ui.screens.statistics

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import cc.wordview.app.ui.activities.lesson.viewmodel.ReviseWord
import cc.wordview.app.ui.activities.statistics.StatisticsActivity
import cc.wordview.app.ui.dtos.LessonToStatisticsCommunicator
import cc.wordview.app.ui.dtos.PlayerToLessonCommunicator
import cc.wordview.gengolex.word.Word
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class StatisticsTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<StatisticsActivity>()

    init {
        PlayerToLessonCommunicator.appendWord(ReviseWord(Word("tear", "lágrima", representable = true)))
        PlayerToLessonCommunicator.appendWord(ReviseWord(Word("rain", "chuva", representable = true)))
        PlayerToLessonCommunicator.appendWord(ReviseWord(Word("hear", "ouvir", representable = true)))
        PlayerToLessonCommunicator.appendWord(ReviseWord(Word("sing", "cantar", representable = true)))

        LessonToStatisticsCommunicator.wordsLearnedAmount = 2

    }

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun renders() {
        composeTestRule.onNodeWithText("+2")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("4")
            .assertExists()
            .assertIsDisplayed()

        // words
        composeTestRule.onNodeWithText("lágrima")
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("chuva")
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("ouvir")
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("cantar")
            .assertExists()
            .assertIsDisplayed()
    }
}