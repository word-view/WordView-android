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

package cc.wordview.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import cc.wordview.app.ui.screens.home.Home

sealed class Screens(val route: String) {
    data object Welcome : Screens("welcome") {
        @Composable
        fun Composable(navHostController: NavHostController) {
            Welcome(navHostController)
        }
    }

    data object Login : Screens("login") {
        @Composable
        fun Composable(navHostController: NavHostController) {
            Login(navHostController)
        }
    }

    data object LanguagePicker : Screens("language-picker") {
        @Composable
        fun Composable(navHostController: NavHostController) {
            LanguagePicker(navHostController)
        }
    }

    data object Settings : Screens("settings") {
        @Composable
        fun Composable(navHostController: NavHostController) {
            Settings(navHostController)
        }
    }

    data object Player : Screens("player") {
        @Composable
        fun Composable(navHostController: NavHostController) {
            Player(navHostController)
        }
    }

    data object Home : Screens("home") {
        @Composable
        fun Composable(navHostController: NavHostController) {
            Home(navHostController)
        }
    }
}