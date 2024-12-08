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

package cc.wordview.app.ui.screens.results

import androidx.lifecycle.ViewModel
import cc.wordview.app.ui.screens.lesson.components.ReviseWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ReviseResultsViewModel : ViewModel() {
    private val _words = MutableStateFlow<List<ReviseWord>>(listOf())
    private val _answeredCorrectly = MutableStateFlow(0)
    private val _answeredWrong = MutableStateFlow(0)

    val words = _words.asStateFlow()

    fun setWords(words: List<ReviseWord>) {
        _words.update { words }
    }

    fun reset() {
        _words.update { listOf() }
        _answeredWrong.update { 0 }
        _answeredCorrectly.update { 0 }
    }
}