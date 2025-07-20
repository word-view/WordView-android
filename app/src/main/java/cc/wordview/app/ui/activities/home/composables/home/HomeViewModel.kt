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

package cc.wordview.app.ui.activities.home.composables.home

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.wordview.app.api.entity.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val homeRepository: HomeRepository
) : ViewModel() {
    private val _editorsPick = MutableStateFlow(ArrayList<Video>())

    val editorsPick = _editorsPick.asStateFlow()

    private var _snackBarMessage = MutableSharedFlow<String>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    fun getHome(context: Context) {
        homeRepository.apply {
            init(context)

            onSucceed = { updateEditorsPick(it) }

            onFail = { s, i ->
                Timber.e("Failed to request home videos \n\t message=$s, status=$i")
                emitMessage(s)
            }

            getHomeVideos()
        }
    }

    fun updateEditorsPick(videos: ArrayList<Video>) {
        _editorsPick.update { videos }
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _snackBarMessage.emit(msg)
        }
    }
}