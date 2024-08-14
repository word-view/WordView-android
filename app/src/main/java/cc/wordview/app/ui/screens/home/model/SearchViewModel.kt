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

package cc.wordview.app.ui.screens.home.model

import androidx.lifecycle.ViewModel
import cc.wordview.app.api.VideoSearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import kotlin.time.Duration.Companion.milliseconds

object SearchViewModel : ViewModel() {
    private val _searching = MutableStateFlow(false)
    private val _query = MutableStateFlow("")
    private val _searchResults = MutableStateFlow(ArrayList<VideoSearchResult>())

    val searching: StateFlow<Boolean> = _searching.asStateFlow()
    val query: StateFlow<String> = _query.asStateFlow()
    val searchResults: StateFlow<ArrayList<VideoSearchResult>> = _searchResults.asStateFlow()

    fun setSearching(value: Boolean) {
        _searching.update { value }
    }

    fun setQuery(query: String) {
        _query.update { query }
    }

    fun setSearchResults(resultList: List<StreamInfoItem>) {
        val parsed = ArrayList<VideoSearchResult>()

        for (item in resultList) {
            parsed.add(VideoSearchResult(
                id = getIdFromUrl(item.url),
                title = item.name,
                channel = item.uploaderName,
                duration = item.duration,
            ))
        }

        _searchResults.update { parsed }
    }

    private fun getIdFromUrl(url: String): String {
        return url.split("watch?v=")[1]
    }
}
