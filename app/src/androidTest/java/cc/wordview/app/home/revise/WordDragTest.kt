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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.subtitle.initializeIcons
import cc.wordview.app.ui.screens.home.ReviseResults
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel
import cc.wordview.app.ui.screens.home.revise.ReviseScreen
import cc.wordview.app.ui.screens.home.revise.WordDrag
import cc.wordview.app.ui.screens.home.revise.algo.ReviseWord
import cc.wordview.app.ui.screens.util.Screen
import cc.wordview.app.ui.theme.WordViewTheme
import cc.wordview.gengolex.languages.Word
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Rule
import org.junit.Test

class WordDragTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = WordReviseViewModel

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

            initializeIcons()

            WordViewTheme {
                ProvidePreferenceLocals {
                    NavHost(navController = navController, startDestination = "word-drag") {
                        composable("word-drag") {
                            WordDrag(navController)
                        }

                        composable(Screen.ReviseResults.route) {
                            ReviseResults(navController)
                        }
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
        composeTestRule.onNodeWithText("lágrima").assertExists()
    }

    @Test
    fun dragUp() {
        setupScreen(appendWords = true)

        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, 500f))
            up()
        }
        composeTestRule.waitUntil(10_000) { viewModel.screen.value == ReviseScreen.Presenter.route }
    }

    @Test
    fun dragDown() {
        setupScreen(appendWords = true)

        composeTestRule.onNodeWithTag("drag").performTouchInput {
            down(center)
            moveBy(Offset(0f, -500f))
            up()
        }
        composeTestRule.waitUntil(10_000) { viewModel.screen.value == ReviseScreen.Presenter.route }
    }
}