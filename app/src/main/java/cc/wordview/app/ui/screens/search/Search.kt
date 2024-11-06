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

package cc.wordview.app.ui.screens.search

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.R
import cc.wordview.app.SongViewModel
import cc.wordview.app.ui.components.CircularProgressIndicator
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.components.ResultItem
import cc.wordview.app.ui.screens.components.Screen
import cc.wordview.app.ui.theme.Typography
import com.gigamole.composefadingedges.verticalFadingEdges

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(navHostController: NavHostController, viewModel: SearchViewModel = hiltViewModel()) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.searchResults.collectAsStateWithLifecycle()
    val searching by viewModel.searching.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }

    val darkTheme = isSystemInDarkTheme()

    fun search(query: String) {
        viewModel.setState(SearchState.LOADING)
        viewModel.setSearching(false)

        viewModel.search(
            query,
            onSuccess = { viewModel.setState(SearchState.COMPLETE) },
            onError = { viewModel.setState(SearchState.ERROR) }
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.testTag("search-input-field"),
                        query = query,
                        onQueryChange = { viewModel.setQuery(it) },
                        onSearch = { search(it) },
                        expanded = searching,
                        onExpandedChange = { viewModel.setSearching(it) },
                        enabled = true,
                        placeholder = { Text("Search for music, artists, albums...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                            )
                        },
                    )
                },
                expanded = searching,
                onExpandedChange = { viewModel.setSearching(it) },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth(0.97F)
                    .testTag("search-bar"),
            ) {

            }
        }
    }) { innerPadding ->
        // For tests to work, the launched effect has to be inside
        // the scaffold (https://issuetracker.google.com/issues/206249038#comment9)
        OneTimeEffect { if (results.isEmpty()) focusRequester.requestFocus() }

        when (state) {
            SearchState.NONE -> {}
            SearchState.LOADING -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(64.dp)
                }
            }

            SearchState.ERROR -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("error"),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        painter = painterResource(id = if (darkTheme) R.drawable.nonet else R.drawable.nonet_dark),
                        contentDescription = null
                    )
                    Spacer(Modifier.size(15.dp))
                    Text(
                        text = "It seems that you are offline",
                        textAlign = TextAlign.Center,
                        style = Typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                }
            }

            SearchState.COMPLETE -> {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalFadingEdges(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    var i = 0

                    items(results, key = { it.id }) {
                        i += 1
                        Spacer(Modifier.size(16.dp))
                        ResultItem(
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(durationMillis = i * 250),
                                placementSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy)
                            ), result = it
                        ) {
                            SongViewModel.setVideo(it.id)
                            navHostController.navigate(Screen.Player.route)
                        }
                    }
                }
            }
        }
    }
}