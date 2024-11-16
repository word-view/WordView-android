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

package cc.wordview.app.ui.screens.revise

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.ui.screens.revise.components.ReviseWord
import cc.wordview.app.ui.screens.components.Screen
import cc.wordview.gengolex.languages.Word
import org.junit.Rule
import org.junit.Test

class LessonTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = LessonViewModel

    private fun setupScreen() {
        viewModel.appendWord(ReviseWord(Word("1", "1")))
        viewModel.appendWord(ReviseWord(Word("2", "2")))
        viewModel.appendWord(ReviseWord(Word("3", "3")))
        viewModel.appendWord(ReviseWord(Word("4", "4")))
        viewModel.appendWord(ReviseWord(Word("5", "5")))

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "word-revise") {
                composable(Screen.WordRevise.route) {
                    Lesson(navController)
                }

                composable("home") { Box {} }
            }
        }
    }

    @Test
    fun renders() {
        setupScreen()
        composeTestRule.onNodeWithTag("back-button").assertExists()
    }

    @Test
    fun performGoBack() {
        setupScreen()
        composeTestRule.onNodeWithTag("back-button").performClick()
        composeTestRule.onNodeWithTag("lesson-quit-alert-dialog").assertExists()
    }
}