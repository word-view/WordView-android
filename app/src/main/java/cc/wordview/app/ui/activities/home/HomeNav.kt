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

package cc.wordview.app.ui.activities.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import cc.wordview.app.ui.activities.home.composables.Profile
import cc.wordview.app.ui.activities.home.composables.Settings
import cc.wordview.app.ui.activities.home.composables.history.History
import cc.wordview.app.ui.activities.home.composables.home.Home
import cc.wordview.app.ui.activities.home.composables.search.Search

/**
 * Contains all the screens that Home can navigate to
 *
 * @property route The route of the screen
 */
sealed class HomeNav(val route: String) {
    @Composable
    open fun Composable(navHostController: NavHostController) {}

    data object Settings : HomeNav("settings") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            Settings(navHostController)
        }
    }

    data object Home : HomeNav("home") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            Home(navHostController)
        }
    }

    data object Search : HomeNav("search") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            Search()
        }
    }

    data object Profile : HomeNav("profile") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            Profile(navHostController)
        }
    }

    data object History : HomeNav("history") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            History(navHostController)
        }
    }


    companion object {
        val screens = listOf(Settings, Home, Search, Profile, History)
    }
}