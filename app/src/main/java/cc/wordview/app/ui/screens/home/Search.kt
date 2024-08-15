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

package cc.wordview.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.SongViewModel
import cc.wordview.app.api.Video
import cc.wordview.app.extractor.search
import cc.wordview.app.ui.components.AsyncComposable
import cc.wordview.app.ui.components.ResultItem
import cc.wordview.app.ui.screens.home.model.SearchViewModel
import cc.wordview.app.ui.screens.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(navHostController: NavHostController, viewModel: SearchViewModel = SearchViewModel) {
    val searchText by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.searchResults.collectAsStateWithLifecycle()
    val searching by viewModel.searching.collectAsStateWithLifecycle()

    var waitingForResponse by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SearchBar(modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(0.97F)
                .testTag("search-bar"),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                placeholder = { Text("Search for music, artists, albums...") },
                query = searchText,
                onQueryChange = { viewModel.setQuery(it) },
                onSearch = {
                    waitingForResponse = true
                    viewModel.setSearching(false)

                    search(it) { r ->
                        viewModel.setSearchResults(r)
                        waitingForResponse = false
                    }
                },
                active = searching,
                onActiveChange = { viewModel.setSearching(it) }) {}
        }
    }) { innerPadding ->
        // For tests to work, the launched effect has to be inside
        // the scaffold (https://issuetracker.google.com/issues/206249038#comment9)
        LaunchedEffect(Unit) { if (results.isEmpty()) focusRequester.requestFocus() }

        AsyncComposable(
            modifier = Modifier.padding(innerPadding),
            condition = !waitingForResponse
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (result in results) {
                    Spacer(Modifier.size(12.dp))
                    ResultItem(result = result) {
                        SongViewModel.setVideo(
                            Video(
                                result.id,
                                result.title,
                                result.channel,
                                "https://img.youtube.com/vi/${result.id}/0.jpg"
                            )
                        )
                        navHostController.navigate(Screen.Player.route)
                    }
                }
            }
        }
    }
}