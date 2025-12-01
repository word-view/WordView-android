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

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.R
import cc.wordview.app.SongViewModel
import cc.wordview.app.components.extensions.openActivity
import cc.wordview.app.components.ui.AsyncImagePlaceholders
import cc.wordview.app.components.ui.OneTimeEffect
import cc.wordview.app.components.ui.Space
import cc.wordview.app.ui.activities.home.composables.history.HistoryEntry
import cc.wordview.app.ui.activities.player.PlayerActivity
import cc.wordview.app.ui.components.SongCard
import cc.wordview.app.ui.theme.Typography
import cc.wordview.app.ui.theme.poppinsFamily
import com.gigamole.composefadingedges.horizontalFadingEdges
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun LearnTab(innerPadding: PaddingValues = PaddingValues(), viewModel: HomeViewModel = hiltViewModel()) {
    val categories by viewModel.homeCategories.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    OneTimeEffect {
        viewModel.getHome()
    }

    PullToRefreshBox(
        modifier = Modifier.padding(innerPadding),
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                viewModel.updateHomeCategories(arrayListOf())
                viewModel.getHome()
                isRefreshing = false
            }
        },
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                state = state,
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(PaddingValues(top = 17.dp))
                .verticalScroll(rememberScrollState())
        ) {
            for (category in categories) {
                Text(
                    text = getStringForId(category.id),
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge,
                    modifier = Modifier.padding(start = 17.dp)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalFadingEdges(),
                    state = rememberLazyListState(),
                ) {
                    item { Spacer(Modifier.size(6.dp)) }

                    var i = 0
                    items(category.entries, key = { it.id }) {
                        i += 1
                        SongCard(
                            modifier = Modifier
                                .testTag("song-card")
                                .animateItem(fadeInSpec = tween(durationMillis = i * 500)),
                            thumbnail = it.cover,
                            artist = it.artist,
                            asyncImagePlaceholders = AsyncImagePlaceholders(
                                noConnectionWhite = R.drawable.nonet,
                                noConnectionDark = R.drawable.nonet_dark
                            ),
                            trackName = it.title
                        ) {
                            SongViewModel.setVideo(it.id)
                            viewModel.saveVideoToHistory(
                                HistoryEntry(
                                    id = it.id,
                                    title = it.title,
                                    artist = it.artist,
                                    thumbnailUrl = it.cover,
                                    duration = 0, // TODO: Hardcode the durations later
                                )
                            )
                            context.openActivity<PlayerActivity>()
                        }
                    }

                    item { Spacer(Modifier.size(128.dp)) }
                }
                Space(24.dp)
            }
        }
    }
}

@Composable
private fun getStringForId(id: String): String {
    val context = LocalContext.current

    return when (id) {
        "editors-pick" -> context.getString(R.string.editors_pick)
        else -> id
    }
}
