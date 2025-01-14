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

package cc.wordview.app.ui.screens.lesson.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import cc.wordview.app.ui.screens.lesson.Drag
import cc.wordview.app.ui.screens.lesson.Presenter
import cc.wordview.app.ui.screens.lesson.Translate

sealed class ReviseScreen(val route: String) {
    @Composable
    open fun Composable(navHostController: NavHostController, innerPadding: PaddingValues) {
    }

    data object Presenter : ReviseScreen("presenter") {
        @Composable
        override fun Composable(navHostController: NavHostController, innerPadding: PaddingValues) {
            Presenter()
        }
    }

    data object IconDrag : ReviseScreen("icon-drag") {
        @Composable
        override fun Composable(navHostController: NavHostController, innerPadding: PaddingValues) {
            Drag(navHostController, mode = DragMode.ICON)
        }
    }

    data object WordDrag : ReviseScreen("word-drag") {
        @Composable
        override fun Composable(navHostController: NavHostController, innerPadding: PaddingValues) {
            Drag(navHostController, mode = DragMode.WORD)
        }
    }

    data object Translate : ReviseScreen("translate") {
        @Composable
        override fun Composable(navHostController: NavHostController, innerPadding: PaddingValues) {
            Translate(innerPadding = innerPadding)
        }
    }

    companion object {
        // Translate is repeated because IconDrag and WordDrag are the same exercise
        // but occupies 2/3 of the probability, making it more common than the translate
        // exercise when it should be a 50/50 chance.
        val screens = listOf(Presenter, IconDrag, WordDrag, Translate, Translate)

        fun getByRoute(route: String): ReviseScreen? {
            for (screen in screens) {
                if (screen.route == route) return screen
            }

            return null
        }

        fun getRandomScreen(): ReviseScreen {
            return screens.filter { s -> s.route != Presenter.route }.random()
        }

        fun getRandomScreen(toFilter: ReviseScreen): ReviseScreen {
            return screens.filter { s -> s.route != Presenter.route }
                .filter { s -> s.route != toFilter.route }.random()
        }
    }
}