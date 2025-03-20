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
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.ComposeTest
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.screens.results.ReviseResults
import cc.wordview.app.ui.screens.lesson.components.ReviseScreen
import cc.wordview.app.ui.screens.lesson.components.ReviseWord
import cc.wordview.app.ui.screens.lesson.components.DragMode
import cc.wordview.app.ui.screens.lesson.model.DragViewModel
import cc.wordview.app.ui.screens.components.Screen
import cc.wordview.gengolex.word.Word
import coil.request.ImageRequest
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordDragTest : ComposeTest() {
    private val viewModel = LessonViewModel

    private fun setupScreen(autoAdvance: Boolean = true) {
        composeTestRule.mainClock.autoAdvance = autoAdvance

        viewModel.appendWord(ReviseWord(Word("tear", "lágrima", representable = true)))
        viewModel.appendWord(ReviseWord(Word("rain", "chuva", representable = true)))
        viewModel.appendWord(ReviseWord(Word("umbrella", "guarda-chuva", representable = true)))

        viewModel.setWord(ReviseWord(Word("tear", "lágrima", representable = true)))

        composeTestRule.setContent {
            val navController = rememberNavController()

            ImageCacheManager.init(LocalContext.current)

            for (word in viewModel.wordsToRevise.value) {
                val request = ImageRequest.Builder(LocalContext.current)
                    .data("http://10.0.2.2:8080/api/v1/image?parent=${word.tokenWord.parent}")
                    .allowHardware(true)
                    .memoryCacheKey(word.tokenWord.parent)

                ImageCacheManager.enqueue(request)
            }

            ProvidePreferenceLocals {
                NavHost(navController = navController, startDestination = "word-drag") {
                    composable("word-drag") {
                        Drag(navController, mode = DragMode.WORD)
                    }

                    composable(Screen.ReviseResults.route) {
                        ReviseResults(navController)
                    }
                }
            }
        }
    }

    @Test
    fun renders() {
        setupScreen()

        composeTestRule.onNodeWithTag("root").assertExists()
        composeTestRule.onNodeWithTag("drag").assertExists()
        composeTestRule.onNodeWithText("lágrima").assertExists()
        composeTestRule.onNodeWithTag("top-word").assertExists()
        composeTestRule.onNodeWithTag("down-word").assertExists()
    }

    @Test
    fun upAndDownNotEqual() {
        setupScreen()
        assertTrue(DragViewModel.topWord.value != DragViewModel.downWord.value)
    }

    @Test
    fun dragUp() {
        setupScreen()

        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, 500f))
            up()
        }
        composeTestRule.waitUntil(10_000) { viewModel.currentScreen.value == ReviseScreen.Presenter.route }
    }

    @Test
    fun dragDown() {
        setupScreen()

        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, -500f))
            up()
        }
        composeTestRule.waitUntil(10_000) { viewModel.currentScreen.value == ReviseScreen.Presenter.route }
    }
}