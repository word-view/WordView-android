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

package cc.wordview.app.ui.screens.home.revise.model

import androidx.lifecycle.ViewModel
import cc.wordview.gengolex.languages.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object IconDragViewModel : ViewModel() {
    private val _topWord = MutableStateFlow<Word?>(null)
    private val _downWord = MutableStateFlow<Word?>(null)

    var topWord = _topWord.asStateFlow()
    val downWord = _downWord.asStateFlow()

    fun setTopWord(word: Word) {
        _topWord.update { word }
    }

    fun setDownWord(word: Word) {
        _downWord.update { word }
    }
}