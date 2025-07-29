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

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import cc.wordview.app.misc.PlayerToLessonCommunicator
import cc.wordview.app.ui.activities.lesson.LessonActivity
import cc.wordview.app.ui.activities.lesson.LessonNav
import cc.wordview.app.ui.activities.lesson.LessonNav.Choose
import cc.wordview.app.ui.activities.lesson.LessonNav.IconDrag
import cc.wordview.app.ui.activities.lesson.LessonNav.ListenIcon
import cc.wordview.app.ui.activities.lesson.LessonNav.ListenWord
import cc.wordview.app.ui.activities.lesson.LessonNav.Presenter
import cc.wordview.app.ui.activities.lesson.LessonNav.WordDrag
import cc.wordview.app.ui.activities.lesson.composables.MeaningPresenter
import cc.wordview.app.ui.activities.lesson.viewmodel.ReviseWord
import cc.wordview.app.ui.activities.lesson.viewmodel.SaveKnownWordsRepository
import cc.wordview.app.ui.activities.lesson.viewmodel.TranslationsRepository
import cc.wordview.gengolex.word.Word
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class LessonTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<LessonActivity>()

    @Inject
    lateinit var saveKnownWordsRepository: SaveKnownWordsRepository

    @Inject
    lateinit var translationsRepository: TranslationsRepository

    init {
        PlayerToLessonCommunicator.appendWord(ReviseWord(Word("tear", "lágrima", representable = true)))
        PlayerToLessonCommunicator.appendWord(ReviseWord(Word("rain", "chuva", representable = true)))
        PlayerToLessonCommunicator.appendWord(ReviseWord(Word("hear", "ouvir", representable = true)))
        PlayerToLessonCommunicator.appendWord(ReviseWord(Word("sing", "cantar", representable = true)))

        LessonNav.screens = listOf(
            IconDrag, WordDrag,
            Choose, Choose,
            ListenIcon, ListenWord,
            Presenter,
            LessonNav.MeaningPresenter
        )
    }

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun renders() {
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.onNodeWithTag("lesson-exercise")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("meaning-presenter")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.mainClock.advanceTimeBy(2_000)

        composeTestRule.onNodeWithTag("word-image")
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("word")
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("translated-word")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.mainClock.advanceTimeBy(10_000)


        val choose = composeTestRule.onAllNodesWithTag("choose").fetchSemanticsNodes()
        val drag = composeTestRule.onAllNodesWithTag("drag").fetchSemanticsNodes()
        val listen = composeTestRule.onAllNodesWithTag("listen").fetchSemanticsNodes()

        var lesson = ""

        if (choose.size == 1) lesson = "choose"
        if (drag.size == 1) lesson = "drag"
        if (listen.size == 1) lesson = "listen"

        when (lesson) {
            "choose" -> {
                composeTestRule.onNodeWithTag("choose")
                    .assertExists()
                    .assertIsDisplayed()
            }
            "drag" -> {
                composeTestRule.onNodeWithTag("drag")
                    .assertExists()
                    .assertIsDisplayed()
            }
            "listen" -> {
                composeTestRule.onNodeWithTag("listen")
                    .assertExists()
                    .assertIsDisplayed()
            }
        }
    }
}