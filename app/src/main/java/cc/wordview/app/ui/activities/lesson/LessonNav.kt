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

package cc.wordview.app.ui.activities.lesson

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import cc.wordview.app.ui.activities.lesson.composables.Drag
import cc.wordview.app.ui.activities.lesson.composables.DragMode
import cc.wordview.app.ui.activities.lesson.composables.Presenter

sealed class LessonNav(val route: String) {
    @Composable
    open fun Composable(innerPadding: PaddingValues) {
    }

    data object IconDrag : LessonNav("icon-drag") {
        @Composable
        override fun Composable(innerPadding: PaddingValues) {
            Drag(DragMode.ICON)
        }
    }

    data object WordDrag : LessonNav("word-drag") {
        @Composable
        override fun Composable(innerPadding: PaddingValues) {
            Drag(DragMode.WORD)
        }
    }

    data object Presenter : LessonNav("presenter") {
        @Composable
        override fun Composable(innerPadding: PaddingValues) {
            Presenter()
        }
    }

    companion object {
        val screens = listOf(IconDrag, WordDrag, Presenter)

        fun getByRoute(route: String): LessonNav? {
            for (screen in screens) {
                if (screen.route == route) return screen
            }

            return null
        }

        fun getRandomScreen(): LessonNav {
            return screens.filter { s -> s.route != Presenter.route }.random()
        }

        fun getRandomScreen(toFilter: LessonNav): LessonNav {
            return screens.filter { s -> s.route != toFilter.route }.random()
        }
    }
}