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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import cc.wordview.app.R
import cc.wordview.app.components.ui.BackTopAppBar
import cc.wordview.app.database.RoomAccess
import cc.wordview.app.database.entity.ViewedVideo
import cc.wordview.app.components.extensions.openActivity
import cc.wordview.app.ui.activities.player.PlayerActivity
import cc.wordview.app.ui.components.HistoryItem
import cc.wordview.app.ui.theme.poppinsFamily
import com.composegears.tiamat.compose.back
import com.composegears.tiamat.compose.navController
import com.composegears.tiamat.compose.navDestination
import com.composegears.tiamat.navigation.NavDestination
import com.gigamole.composefadingedges.verticalFadingEdges
import kotlin.uuid.Uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
val HistoryScreen: NavDestination<Unit> by navDestination {
    val navController = navController()
    val listState = rememberLazyListState()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var history = remember { mutableStateListOf<ViewedVideo>() }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            val database = RoomAccess.getDatabase()
            val videos = withContext(Dispatchers.IO) { database.viewedVideoDao().getAll() }
            history.clear()
            history.addAll(videos)
        }
    }

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

            items(history.toList().reversed(), key = { Uuid.random() }) {
                i += 1
                HistoryItem(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = i * 250),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    result = it
                ) {
                    context.openActivity<PlayerActivity>(
                        "id" to it.id,
                        "title" to it.title,
                        "artist" to it.artist,
                    )
                }
                Spacer(Modifier.size(16.dp))
            }

            item { Spacer(Modifier.size(128.dp)) }
        }
    }
}
