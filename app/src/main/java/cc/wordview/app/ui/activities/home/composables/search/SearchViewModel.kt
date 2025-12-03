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
import cc.wordview.app.BuildConfig
import cc.wordview.app.R
import cc.wordview.app.api.VideoSearchResult
import cc.wordview.app.components.extensions.without
import cc.wordview.app.dataStore
import cc.wordview.app.ui.activities.home.composables.history.HistoryEntry
import cc.wordview.app.ui.activities.home.composables.history.PLAY_HISTORY
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
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
import kotlin.collections.listOf

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _searching = MutableStateFlow(false)
    private val _animateSearch = MutableStateFlow(true)
    private val _query = MutableStateFlow("")
    private val _searchResults = MutableStateFlow(ArrayList<VideoSearchResult>())
    private val _state = MutableStateFlow(SearchState.NONE)
    private val _providedLyrics = MutableStateFlow(listOf<String>())

    val searching = _searching.asStateFlow()
    val animateSearch = _animateSearch.asStateFlow()
    val query = _query.asStateFlow()
    val searchResults = _searchResults.asStateFlow()
    val state = _state.asStateFlow()
    val providedLyrics = _providedLyrics.asStateFlow()

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
                        appContext.getString(R.string.no_connection)
                    } else {
                        e.message ?: e.toString()
                    }

                    onError(message)
                }
            }
        }
    }

    fun getProvidedLyrics() {
        val queue = Volley.newRequestQueue(appContext)
        val endpoint = BuildConfig.API_BASE_URL
        val url = "$endpoint/api/v1/lyrics/list"

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { jsonArray ->
                val list = List(jsonArray.length()) { jsonArray.getString(it) }
                _providedLyrics.update { list }

                Timber.v("providedLyrics.size=${_providedLyrics.value.size}")
            },
            { error ->
                Timber.e("Failed to download server lyrics: ${error.message}")
            }
        )

        queue.add(request)
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

    fun saveSearch(query: String) = viewModelScope.launch {
        appContext.dataStore.edit { preferences ->
            val current = preferences[SEARCH_HISTORY] ?: emptySet()
            if (!current.contains(query))
                preferences[SEARCH_HISTORY] = current + query
        }
    }

    fun saveVideoToHistory(searchResult: VideoSearchResult) = viewModelScope.launch {
        val gson = Gson()
        val historyEntryJson = gson.toJson(HistoryEntry.fromSearchResult(searchResult))

        appContext.dataStore.edit { preferences ->
            val current = preferences[PLAY_HISTORY] ?: emptySet()
            if (!current.contains(historyEntryJson))
                preferences[PLAY_HISTORY] = current + historyEntryJson
        }
    }

    fun removeSearch(searchEntry: String) = viewModelScope.launch {
        appContext.dataStore.edit { preferences ->
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
                artist = item.uploaderName,
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
                artist = item.uploaderName,
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