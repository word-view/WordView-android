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

package cc.wordview.app.ui.screens.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import cc.wordview.app.ui.screens.Settings
import cc.wordview.app.ui.screens.home.Home
import cc.wordview.app.ui.screens.results.ReviseResults
import cc.wordview.app.ui.screens.player.Player
import cc.wordview.app.ui.screens.lesson.Lesson
import cc.wordview.app.ui.screens.search.Search

sealed class Screen(val route: String) {
    @Composable
    open fun Composable(navHostController: NavHostController) {}

    data object Settings : Screen("settings") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            Settings(navHostController)
        }
    }

    data object Player : Screen("player") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            Player(navHostController)
        }
    }

    data object Home : Screen("home") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            Home(navHostController)
        }
    }

    data object Search : Screen("search") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            Search(navHostController)
        }
    }

    data object WordRevise : Screen("word-revise") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            Lesson(navHostController)
        }
    }

    data object ReviseResults : Screen("revise-results") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            ReviseResults(navHostController)
        }
    }

    companion object {
        val screens = listOf(Settings, Player, Home, Search, WordRevise, ReviseResults)
    }
}