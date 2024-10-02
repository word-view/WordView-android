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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.SongViewModel
import cc.wordview.app.extensions.goBack
import cc.wordview.app.extractor.VideoStream
import cc.wordview.app.ui.components.Loader
import cc.wordview.app.ui.components.FadeOutBox
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.components.PlayerButton
import cc.wordview.app.ui.components.TextCue
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import cc.wordview.app.ui.screens.home.player.ErrorScreen
import cc.wordview.app.ui.screens.home.player.PlayerState
import cc.wordview.app.ui.screens.home.player.PlayerStatus
import cc.wordview.app.ui.screens.util.Screen
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.LocalPreferenceFlow

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun Player(
    navHostController: NavHostController,
    viewModel: PlayerViewModel = PlayerViewModel,
    autoplay: Boolean = true
) {
    val videoId by SongViewModel.videoId.collectAsStateWithLifecycle()
    val finalized by viewModel.finalized.collectAsStateWithLifecycle()
    val status by viewModel.playerStatus.collectAsStateWithLifecycle()

    val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()

    val context = LocalContext.current as Activity

    val playerState = remember { PlayerState(preferences) }

    fun cleanup() {
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        viewModel.reset()
    }

    OneTimeEffect {
        CoroutineScope(Dispatchers.IO).launch {
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

            SongViewModel.setVideoStream(VideoStream())
            SongViewModel.videoStream.value.init(videoId)

            playerState.setup({ cleanup() }, context)
        }
    }

    LaunchedEffect(finalized) {
        if (finalized) {
            navHostController.navigate(Screen.WordRevise.route)
            viewModel.unFinalize()
        }
    }

    BackHandler {
        cleanup()
        navHostController.goBack()
    }

    Scaffold { innerPadding ->
        when (status) {
            PlayerStatus.ERROR -> ErrorScreen({ cleanup() }, navHostController)
            PlayerStatus.LOADING -> Loader()
            PlayerStatus.READY -> PlayerContent(
                innerPadding,
                autoplay,
                navHostController,
                { cleanup() })
        }
    }
}

@Composable
private fun PlayerContent(
    innerPadding: PaddingValues,
    autoplay: Boolean,
    navHostController: NavHostController,
    cleanup: () -> Unit,
    viewModel: PlayerViewModel = PlayerViewModel
) {
    val videoStream by SongViewModel.videoStream.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle()
    val currentCue by viewModel.currentCue.collectAsStateWithLifecycle()
    val playIcon by viewModel.playIcon.collectAsStateWithLifecycle()

    OneTimeEffect { if (autoplay) player.togglePlay() }

    Box(
        Modifier
            .fillMaxSize()
            .testTag("player-interface")
    ) {
        AsyncImage(
            model = videoStream.getHQThumbnail(),
            contentDescription = "${videoStream.getTitle()} cover",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.15f),
            contentScale = ContentScale.FillWidth,
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            TextCue(
                modifier = Modifier
                    .zIndex(1f)
                    .padding(bottom = 6.dp)
                    .testTag("text-cue"),
                cue = currentCue
            )
        }
        // Box Controls overlay
        FadeOutBox(duration = 250, stagnationTime = 3000) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(innerPadding),
                contentAlignment = Alignment.TopStart,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { cleanup(); navHostController.goBack(); },
                        modifier = Modifier.testTag("back-button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = videoStream.info.name,
                        fontSize = 18.sp
                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    Modifier.fillMaxWidth(0.5f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerButton(
                        icon = Icons.Filled.SkipPrevious,
                        size = 72.dp,
                        modifier = Modifier.testTag("skip-back")
                    ) {
                        player.skipBackward()
                    }
                    PlayerButton(
                        icon = playIcon,
                        size = 80.dp,
                        modifier = Modifier.testTag("play")
                    ) {
                        player.togglePlay()
                    }
                    PlayerButton(
                        icon = Icons.Filled.SkipNext,
                        size = 72.dp,
                        modifier = Modifier.testTag("skip-forward")
                    ) {
                        player.skipForward()
                    }
                }
            }
        }
    }
}