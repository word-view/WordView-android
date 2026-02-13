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

package cc.wordview.app.ui.activities.home.composables.history

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringSetPreferencesKey
import cc.wordview.app.R
import cc.wordview.app.SongViewModel
import cc.wordview.app.components.extensions.openActivity
import cc.wordview.app.components.ui.BackTopAppBar
import cc.wordview.app.dataStore
import cc.wordview.app.ui.activities.player.PlayerActivity
import cc.wordview.app.ui.components.HistoryItem
import cc.wordview.app.ui.theme.poppinsFamily
import com.composegears.tiamat.compose.back
import com.composegears.tiamat.compose.navController
import com.composegears.tiamat.compose.navDestination
import com.composegears.tiamat.navigation.NavDestination
import com.gigamole.composefadingedges.verticalFadingEdges
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid
import com.google.gson.Gson
import kotlin.uuid.ExperimentalUuidApi

val PLAY_HISTORY = stringSetPreferencesKey("play_history")

@OptIn(ExperimentalUuidApi::class)
val HistoryScreen: NavDestination<Unit> by navDestination {
    val navController = navController()
    val listState = rememberLazyListState()

    val context = LocalContext.current

    val playHistoryFlow = remember { context.dataStore.data.map { it[PLAY_HISTORY] ?: emptySet() } }
    val playHistory by playHistoryFlow.collectAsState(initial = emptySet())

    val gson = remember { Gson() }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        BackTopAppBar(
            title = {
                Text(
                    stringResource(R.string.history),
                    fontFamily = poppinsFamily,
                )
            },
            onClickBack = { navController.back() }
        )
    }) { innerPadding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalFadingEdges(),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState
        ) {
            item { Spacer(Modifier.size(16.dp)) }
            var i = 0

            items(playHistory.toList().reversed(), key = { Uuid.random() }) {
                val entry = gson.fromJson(it, HistoryEntry::class.java)

                i += 1
                HistoryItem(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = i * 250),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    result = entry
                ) {
                    SongViewModel.setVideo(entry.id)
                    context.openActivity<PlayerActivity>()
                }
                Spacer(Modifier.size(16.dp))
            }

            item { Spacer(Modifier.size(128.dp)) }
        }
    }
}