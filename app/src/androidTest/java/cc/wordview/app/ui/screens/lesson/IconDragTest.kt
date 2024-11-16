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

package cc.wordview.app.ui.screens.lesson

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.ui.screens.results.ReviseResults
import cc.wordview.app.ui.screens.lesson.components.ReviseScreen
import cc.wordview.app.ui.screens.lesson.components.ReviseWord
import cc.wordview.app.ui.screens.lesson.components.DragMode
import cc.wordview.app.ui.screens.lesson.model.DragViewModel
import cc.wordview.app.ui.screens.components.Screen
import cc.wordview.gengolex.languages.Word
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class IconDragTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = LessonViewModel

    private fun setupScreen(autoAdvance: Boolean = true, appendWords: Boolean = false) {
        composeTestRule.mainClock.autoAdvance = autoAdvance

        if (appendWords) {
            viewModel.appendWord(ReviseWord(Word("tear", "lágrima")))
            viewModel.appendWord(ReviseWord(Word("rain", "chuva")))
            viewModel.appendWord(ReviseWord(Word("voice", "voz")))

            viewModel.setWord(ReviseWord(Word("tear", "lágrima")))
        }

        composeTestRule.setContent {
            val navController = rememberNavController()

            ProvidePreferenceLocals {
                NavHost(navController = navController, startDestination = "icon-drag") {
                    composable("icon-drag") {
                        Drag(navController, mode = DragMode.ICON)
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
        setupScreen(appendWords = true)

        composeTestRule.onNodeWithTag("root").assertExists()
        composeTestRule.onNodeWithTag("drag").assertExists()
        composeTestRule.onNodeWithTag("top-word").assertExists()
        composeTestRule.onNodeWithTag("down-word").assertExists()

    }

    @Test
    fun upAndDownNotEqual() {
        setupScreen(appendWords = true)
        assertTrue(DragViewModel.topWord.value != DragViewModel.downWord.value)
    }

    @Test
    fun dragUp() {
        setupScreen(appendWords = true)

        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, 500f))
            up()
        }
        composeTestRule.waitUntil(10_000) { viewModel.currentScreen.value == ReviseScreen.Presenter.route }
    }

    @Test
    fun dragDown() {
        setupScreen(appendWords = true)

        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, -500f))
            up()
        }
        composeTestRule.waitUntil(10_000) { viewModel.currentScreen.value == ReviseScreen.Presenter.route }
    }
}