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

package cc.wordview.app.ui.screens.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cc.wordview.assis.book.epub.EpubBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

class ReaderViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    ViewModel() {

    private val _book = MutableStateFlow<EpubBook?>(null)
    private val _uiVisible = MutableStateFlow(false)

    val book = _book.asStateFlow()
    val uiVisible = _uiVisible.asStateFlow()

    fun setBook(book: EpubBook) {
        _book.update {
            Timber.i("Book updated from '${it?.metadata?.title}' to '${book.metadata.title}'")
            book
        }
    }

    fun toggleUi() {
        _uiVisible.update { !it }
    }
}