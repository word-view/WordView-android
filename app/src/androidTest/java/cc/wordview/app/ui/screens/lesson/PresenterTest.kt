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

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import cc.wordview.app.ComposeTest
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.activities.lesson.composables.Presenter
import cc.wordview.app.ui.activities.lesson.viewmodel.Answer
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.activities.lesson.viewmodel.ReviseWord
import cc.wordview.gengolex.word.Word
import coil3.request.ImageRequest
import coil3.request.allowHardware
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Test

class PresenterTest : ComposeTest() {
    private val viewModel = LessonViewModel

    private fun setupScreen(autoAdvance: Boolean = true) {
        composeTestRule.mainClock.autoAdvance = autoAdvance
        viewModel.setWord(ReviseWord(Word("rain", "chuva")))
        composeTestRule.setContent {
            val request = ImageRequest.Builder(LocalContext.current)
                .data("http://10.0.2.2:8080/api/v1/image?parent=rain")
                .allowHardware(true)
                .memoryCacheKey("rain")

            ProvidePreferenceLocals {
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    ImageCacheManager.init(context)
                    ImageCacheManager.enqueue(request)
                    ImageCacheManager.executeAllInQueue()
                }

                Presenter()
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
        viewModel.setAnswer(Answer.WRONG)
        setupScreen(false)
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.onNodeWithTag("wrong").assertExists()
        composeTestRule.mainClock.advanceTimeBy(2000)
        composeTestRule.onNodeWithTag("word").assertExists()
        composeTestRule.onNodeWithText("chuva").assertExists()
    }
}