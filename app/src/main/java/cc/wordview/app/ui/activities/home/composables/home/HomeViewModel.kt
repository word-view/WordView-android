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
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.wordview.app.R
import cc.wordview.app.api.entity.HomeCategory
import cc.wordview.app.ui.activities.home.composables.history.HistoryEntry
import cc.wordview.app.ui.activities.home.composables.history.PLAY_HISTORY
import cc.wordview.app.dataStore
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.plus

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _homeCategories = MutableStateFlow(ArrayList<HomeCategory>())

    val homeCategories = _homeCategories.asStateFlow()

    private var _snackBarMessage = MutableSharedFlow<String>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    fun getHome() {
        updateHomeCategories(arrayListOf())

        homeRepository.apply {
            onSucceed = {
                updateHomeCategories(it)
            }

            onFail = { s, i ->
                Timber.e("Failed to request home videos \n\t message=$s, status=$i")
                
                if (s.contains("UnknownHostException:") || s.contains("ConnectException:") ) {
                    emitMessage(context.getString(R.string.no_connection))
                } else {
                    emitMessage(s)
                }
            }

            getHomeVideos()
        }
    }

    fun updateHomeCategories(videos: ArrayList<HomeCategory>) {
        _homeCategories.update { videos }
    }

    fun saveVideoToHistory(historyEntry: HistoryEntry) = viewModelScope.launch {
        val gson = Gson()
        val historyEntryJson = gson.toJson(historyEntry)

        Timber.v("historyEntryJson=$historyEntryJson")

        context.dataStore.edit { preferences ->
            val current = preferences[PLAY_HISTORY] ?: emptySet()
            if (!current.contains(historyEntryJson))
                preferences[PLAY_HISTORY] = current + historyEntryJson
        }
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _snackBarMessage.emit(msg)
        }
    }
}