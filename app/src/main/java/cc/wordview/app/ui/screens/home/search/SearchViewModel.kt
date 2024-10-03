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

package cc.wordview.app.ui.screens.home.search

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.wordview.app.api.VideoSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val _searching = MutableStateFlow(false)
    private val _query = MutableStateFlow("")
    private val _searchResults = MutableStateFlow(ArrayList<VideoSearchResult>())
    private val _state = MutableStateFlow(SearchState.NONE)

    val searching = _searching.asStateFlow()
    val query = _query.asStateFlow()
    val searchResults = _searchResults.asStateFlow()
    val state = _state.asStateFlow()

    fun setState(value: SearchState) {
        _state.update { value }
    }

    fun setSearching(value: Boolean) {
        _searching.update { value }
    }

    fun setQuery(query: String) {
        _query.update { query }
    }

    fun search(query: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val results = searchRepository.search(query)
                    setSearchResults(results)
                    onSuccess()
                } catch (e: Throwable) {
                    Log.e("AA", "AAA", e)
                    onError()
                }
            }
        }
    }

    private fun setSearchResults(resultList: List<StreamInfoItem>) {
        val parsed = ArrayList<VideoSearchResult>()

        for (item in resultList) {
            parsed.add(VideoSearchResult(
                id = getIdFromUrl(item.url),
                title = item.name,
                channel = item.uploaderName,
                channelIsVerified = item.isUploaderVerified,
                duration = item.duration,
            ))
        }

        _searchResults.update { parsed }
    }

    private fun getIdFromUrl(url: String): String {
        return url.split("watch?v=")[1]
    }
}