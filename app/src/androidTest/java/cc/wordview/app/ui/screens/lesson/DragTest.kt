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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import cc.wordview.app.ComposeTest
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.activities.lesson.LessonNav
import cc.wordview.app.ui.activities.lesson.composables.Drag
import cc.wordview.app.ui.activities.lesson.composables.LessonMode
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.activities.lesson.viewmodel.ReviseWord
import cc.wordview.gengolex.word.Word
import coil3.request.ImageRequest
import coil3.request.allowHardware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Test

class DragTest : ComposeTest() {
    private val viewModel = LessonViewModel

    private fun setupScreen(mode: LessonMode = LessonMode.ICON) {
        viewModel.appendWord(ReviseWord(Word("tear", "lágrima", representable = true)))
        viewModel.appendWord(ReviseWord(Word("rain", "chuva", representable = true)))

        viewModel.setWord(ReviseWord(Word("tear", "lágrima", representable = true)))

        composeTestRule.setContent {
            ImageCacheManager.init(LocalContext.current)

            for (word in viewModel.wordsToRevise.value) {
                val request = ImageRequest.Builder(LocalContext.current)
                    .data("http://10.0.2.2:8080/api/v1/image?parent=${word.tokenWord.parent}")
                    .allowHardware(true)
                    .memoryCacheKey(word.tokenWord.parent)

                ImageCacheManager.enqueue(request)
            }

            CoroutineScope(Dispatchers.Main).launch {
                ImageCacheManager.executeAllInQueue()
            }

            ProvidePreferenceLocals {
                Drag(mode)
            }
        }
    }

    @Test
    fun renders_Icon() {
        setupScreen()

        composeTestRule.onNodeWithTag("root").assertExists()
        composeTestRule.onNodeWithTag("drag").assertExists()
        composeTestRule.onNodeWithTag("top-word").assertExists()
        composeTestRule.onNodeWithTag("down-word").assertExists()
    }

    @Test
    fun renders_Word() {
        setupScreen(LessonMode.WORD)

        composeTestRule.onNodeWithTag("root").assertExists()
        composeTestRule.onNodeWithTag("drag").assertExists()
        composeTestRule.onNodeWithTag("top-word").assertExists()
        composeTestRule.onNodeWithTag("down-word").assertExists()
    }

    @Test
    fun upAndDownNotEqual_Icon() {
        setupScreen()

        composeTestRule.onAllNodesWithText("chuva").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("lágrima").assertCountEquals(1)
    }

    @Test
    fun dragUp_Icon() {
        setupScreen()

        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, 500f))
            up()
        }

        composeTestRule.waitUntil(10_000) { viewModel.currentScreen.value == LessonNav.Presenter.route }
    }

    @Test
    fun dragUp_Word() {
        setupScreen(LessonMode.WORD)

        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, 500f))
            up()
        }

        composeTestRule.waitUntil(10_000) { viewModel.currentScreen.value == LessonNav.Presenter.route }
    }

    @Test
    fun dragDown_Icon() {
        setupScreen()

        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, -500f))
            up()
        }

        composeTestRule.waitUntil(10_000) { viewModel.currentScreen.value == LessonNav.Presenter.route }
    }

    @Test
    fun dragDown_Word() {
        setupScreen(LessonMode.WORD)

        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, -500f))
            up()
        }

        composeTestRule.waitUntil(10_000) { viewModel.currentScreen.value == LessonNav.Presenter.route }
    }
}