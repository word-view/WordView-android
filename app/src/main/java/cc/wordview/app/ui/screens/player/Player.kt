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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.SongViewModel
import cc.wordview.app.extensions.enterImmersiveMode
import cc.wordview.app.extensions.getCleanUploaderName
import cc.wordview.app.extensions.getOrDefault
import cc.wordview.app.extensions.goBack
import cc.wordview.app.extensions.leaveImmersiveMode
import cc.wordview.app.extensions.setOrientationSensorLandscape
import cc.wordview.app.extensions.setOrientationUnspecified
import cc.wordview.app.extractor.VideoStream
import cc.wordview.app.ui.components.CircularProgressIndicator
import cc.wordview.app.ui.components.FadeInAsyncImage
import cc.wordview.app.ui.components.FadeOutBox
import cc.wordview.app.ui.components.NotEnoughWordsDialog
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.components.PlayerButton
import cc.wordview.app.ui.components.Seekbar
import cc.wordview.app.ui.components.TextCue
import cc.wordview.app.ui.screens.components.KeepScreenOn
import cc.wordview.app.ui.screens.components.Screen
import cc.wordview.gengolex.Language
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.LocalPreferenceFlow
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Player(
    navHostController: NavHostController,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val videoId by SongViewModel.videoId.collectAsStateWithLifecycle()
    val videoStream by SongViewModel.videoStream.collectAsStateWithLifecycle()

    val status by viewModel.playerStatus.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle()
    val currentCue by viewModel.currentCue.collectAsStateWithLifecycle()
    val playIcon by viewModel.playIcon.collectAsStateWithLifecycle()
    val finalized by viewModel.finalized.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isBuffering.collectAsStateWithLifecycle()
    val notEnoughWords by viewModel.notEnoughWords.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val statusCode by viewModel.statusCode.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val bufferedPercentage by viewModel.bufferedPercentage.collectAsStateWithLifecycle()

    val activity = LocalContext.current as Activity
    val context = LocalContext.current

    val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()
    val composerMode = remember { preferences.getOrDefault<Boolean>("composer_mode") }
    val langTag = remember { preferences.getOrDefault<String>("language") }

    val systemUiController = rememberSystemUiController()

    OneTimeEffect {
        val lang = Language.byTag(langTag)

        Timber.i("Chosen language is ${lang.name.lowercase()}")

        activity.setOrientationSensorLandscape()

        CoroutineScope(Dispatchers.IO).launch {
            SongViewModel.videoStream.value.init(videoId, context)

            viewModel.initAudio(videoStream.getStreamURL(), context)
            viewModel.getLyrics(preferences, context, videoId, lang, videoStream)
        }
    }

    LaunchedEffect(finalized) {
        if (finalized) {
            activity.setOrientationUnspecified()
            SongViewModel.setVideoStream(VideoStream())
            player.stop()
            systemUiController.leaveImmersiveMode()
            navHostController.navigate(Screen.WordRevise.route)
        }
    }

    fun back() {
        activity.setOrientationUnspecified()
        SongViewModel.setVideoStream(VideoStream())
        player.stop()
        systemUiController.leaveImmersiveMode()
        navHostController.goBack()
    }

    BackHandler { back() }

    KeepScreenOn()

    if (notEnoughWords) NotEnoughWordsDialog { back() }

    Scaffold { innerPadding ->
        when (status) {
            PlayerStatus.READY -> {
                OneTimeEffect {
                    player.play()
                    // For some reason putting this inside the outer OneTimeEffect
                    // doest work on some API levels so this needs to be here
                    systemUiController.enterImmersiveMode()
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("interface")
                ) {
                    FadeInAsyncImage(videoStream.getHQThumbnail())
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        TextCue(
                            modifier = Modifier
                                .zIndex(1f)
                                .padding(bottom = innerPadding.calculateBottomPadding() + 6.dp)
                                .testTag("text-cue"),
                            cue = currentCue
                        )
                    }

                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (isBuffering) CircularProgressIndicator(64.dp)
                    }

                    FadeOutBox(duration = 250, stagnationTime = if (composerMode) 5000 * 10 else 5000) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(TopAppBarDefaults.TopAppBarExpandedHeight)
                                .padding(innerPadding),
                            contentAlignment = Alignment.TopStart,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { back() },
                                    modifier = Modifier.testTag("back-button"),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                                Column {
                                    Text(
                                        text = videoStream.info.name,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = videoStream.info.getCleanUploaderName(),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                        Seekbar(
                            Modifier.padding(top = TopAppBarDefaults.TopAppBarExpandedHeight),
                            composerMode,
                            currentPosition,
                            player.getDuration(),
                            bufferedPercentage
                        )
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
                                    modifier = Modifier.testTag("skip-back"),
                                    icon = Icons.Filled.SkipPrevious,
                                    size = 72.dp,
                                    onClick = { player.skipBack() }
                                )
                                PlayerButton(
                                    modifier = Modifier
                                        .testTag("toggle-play")
                                        .alpha(if (isBuffering) 0.0f else 1.0f),
                                    icon = playIcon,
                                    size = 80.dp,
                                    onClick = { player.play() }
                                )
                                PlayerButton(
                                    modifier = Modifier.testTag("skip-forward"),
                                    icon = Icons.Filled.SkipNext,
                                    size = 72.dp,
                                    onClick = { player.skipForward() }
                                )
                            }
                        }
                    }
                }
            }

            PlayerStatus.ERROR -> ErrorScreen(navHostController, errorMessage, statusCode)

            PlayerStatus.LOADING -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(64.dp)
                }
            }
        }
    }
}