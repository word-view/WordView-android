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

package cc.wordview.app.ui.activities.home.composables.search

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.wordview.app.R
import cc.wordview.app.api.VideoSearchResult
import cc.wordview.app.extensions.without
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _searching = MutableStateFlow(false)
    private val _animateSearch = MutableStateFlow(true)
    private val _query = MutableStateFlow("")
    private val _searchResults = MutableStateFlow(ArrayList<VideoSearchResult>())
    private val _state = MutableStateFlow(SearchState.NONE)

    val searching = _searching.asStateFlow()
    val animateSearch = _animateSearch.asStateFlow()
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

    fun search(query: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _animateSearch.update { true }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val results = searchRepository.search(query)
                    onSuccess()
                    // if the search results are instantly populated the animation won't work
                    delay(50L)
                    setSearchResults(results)
                } catch (e: Throwable) {
                    Timber.e(e)

                    val message = if ((e.message ?: e.toString()).contains("No address associated")) {
                        context.getString(R.string.no_connection)
                    } else {
                        e.message ?: e.toString()
                    }

                    onError(message)
                }
            }
        }
    }

    fun searchNextPage(query: String) {
        _animateSearch.update { false }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val results = searchRepository.searchNextPage(query)
                    appendSearchResults(results)
                } catch (e: Throwable) {
                    Timber.e(e)
                }
            }
        }
    }

    fun saveSearch(context: Context, searchEntry: String) = viewModelScope.launch {
        context.dataStore.edit { preferences ->
            val current = preferences[SEARCH_HISTORY] ?: emptySet()
            if (!current.contains(searchEntry))
                preferences[SEARCH_HISTORY] = current + searchEntry
        }
    }

    fun removeSearch(context: Context, searchEntry: String) = viewModelScope.launch {
        context.dataStore.edit { preferences ->
            val current = preferences[SEARCH_HISTORY] ?: emptySet()
            preferences[SEARCH_HISTORY] = current.without(searchEntry)
        }
    }

    private fun setSearchResults(resultList: List<StreamInfoItem>) {
        val parsed = ArrayList<VideoSearchResult>()

        for (item in resultList) {
            parsed.add(VideoSearchResult(
                id = getIdFromUrl(item.url),
                title = item.name,
                channel = item.uploaderName,
                thumbnails = item.thumbnails,
                channelIsVerified = item.isUploaderVerified,
                duration = item.duration,
            ))
        }

        _searchResults.update { parsed }
    }

    private fun appendSearchResults(resultList: List<StreamInfoItem>) {
        val parsed = ArrayList<VideoSearchResult>()

        for (item in resultList) {
            parsed.add(VideoSearchResult(
                id = getIdFromUrl(item.url),
                title = item.name,
                channel = item.uploaderName,
                thumbnails = item.thumbnails,
                channelIsVerified = item.isUploaderVerified,
                duration = item.duration,
            ))
        }

        _searchResults.update { oldList ->
            ArrayList<VideoSearchResult>(oldList).apply { addAll(parsed) }.distinct() as ArrayList<VideoSearchResult>
        }
    }

    private fun getIdFromUrl(url: String): String {
        return url.split("watch?v=")[1]
    }
}