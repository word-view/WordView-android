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

package cc.wordview.app.ui.screens.player

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.SongViewModel
import cc.wordview.app.extensions.goBack
import cc.wordview.app.extensions.setOrientationSensorLandscape
import cc.wordview.app.extensions.setOrientationUnspecified
import cc.wordview.app.extractor.VideoStream
import cc.wordview.app.ui.components.FadeOutBox
import cc.wordview.app.ui.components.Loader
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.components.PlayerButton
import cc.wordview.app.ui.components.TextCue
import cc.wordview.app.ui.screens.components.Screen
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.LocalPreferenceFlow

@Composable
fun Player(
    navHostController: NavHostController,
    viewModel: PlayerViewModel = hiltViewModel(),
    autoplay: Boolean = true
) {
    val videoId by SongViewModel.videoId.collectAsStateWithLifecycle()
    val videoStream by SongViewModel.videoStream.collectAsStateWithLifecycle()

    val status by viewModel.playerStatus.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle()
    val currentCue by viewModel.currentCue.collectAsStateWithLifecycle()
    val playIcon by viewModel.playIcon.collectAsStateWithLifecycle()
    val finalized by viewModel.finalized.collectAsStateWithLifecycle()

    val activity = LocalContext.current as Activity
    val context = LocalContext.current

    val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()

    OneTimeEffect {
        activity.setOrientationSensorLandscape()
        CoroutineScope(Dispatchers.IO).launch {
            SongViewModel.setVideoStream(VideoStream())
            SongViewModel.videoStream.value.init(videoId)

            viewModel.initAudio(videoStream)
            viewModel.getLyrics(preferences, context, videoId, "ja", videoStream.searchQuery)
        }
    }

    LaunchedEffect(finalized) {
        if (finalized) {
            activity.setOrientationUnspecified()
            player.stop()
            navHostController.navigate(Screen.WordRevise.route)
        }
    }

    fun back() {
        activity.setOrientationUnspecified()
        player.stop()
        navHostController.goBack()
    }

    BackHandler { back() }

    Scaffold { innerPadding ->
        when (status) {
            PlayerStatus.ERROR -> ErrorScreen(
                { activity.setOrientationUnspecified() },
                navHostController
            )

            PlayerStatus.LOADING -> Loader()

            PlayerStatus.READY -> {
                OneTimeEffect { if (autoplay) viewModel.togglePlay() }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("interface")
                ) {
                    AsyncImage(
                        model = videoStream.getHQThumbnail(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.15f),
                        contentScale = ContentScale.FillWidth
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
                                    onClick = { back() },
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
                                ) {
                                    player.skipBackward()
                                }
                                PlayerButton(
                                    icon = playIcon,
                                    size = 80.dp,
                                ) {
                                    viewModel.togglePlay()
                                }
                                PlayerButton(
                                    icon = Icons.Filled.SkipNext,
                                    size = 72.dp,
                                ) {
                                    player.skipForward()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}