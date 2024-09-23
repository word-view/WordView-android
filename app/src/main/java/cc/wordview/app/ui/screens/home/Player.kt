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
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.R
import cc.wordview.app.SongViewModel
import cc.wordview.app.api.handler.PlayerRequestHandler
import cc.wordview.app.extensions.goBack
import cc.wordview.app.extractor.getStreamFrom
import cc.wordview.app.extractor.getSubtitleFor
import cc.wordview.app.subtitle.getIconForWord
import cc.wordview.app.ui.components.AsyncComposable
import cc.wordview.app.ui.components.FadeOutBox
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.components.PlayerButton
import cc.wordview.app.ui.components.TextCue
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel
import cc.wordview.app.ui.screens.home.revise.components.ReviseWord
import cc.wordview.app.ui.screens.util.Screen
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.compose.preference.LocalPreferenceFlow

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Player(
    navHostController: NavHostController,
    viewModel: PlayerViewModel = PlayerViewModel,
    requestHandler: PlayerRequestHandler = PlayerRequestHandler,
    autoplay: Boolean = false
) {
    val TAG = "Player"

    val song by SongViewModel.video.collectAsStateWithLifecycle()
    val audioInitFailed by viewModel.audioInitFailed.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle()
    val cues by viewModel.cues.collectAsStateWithLifecycle()
    val currentCue by viewModel.currentCue.collectAsStateWithLifecycle()
    val playIcon by viewModel.playIcon.collectAsStateWithLifecycle()

    val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    var audioReady by rememberSaveable { mutableStateOf(false) }
    var lyricsReady by rememberSaveable { mutableStateOf(false) }
    var dictionaryReady by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current as Activity

    val systemUiController = rememberSystemUiController()

    fun cleanup() {
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        systemUiController.isSystemBarsVisible = true

        viewModel.reset()
    }

    fun setup() {
        coroutineScope.launch {
            player.apply {
                onPositionChange = {
                    // the use of `viewModel.lyrics` is because `lyrics.collectAsStateWithLifecycle()`
                    // does not update here and end up using the old lyrics value which is empty.
                    viewModel.setCurrentCue(viewModel.lyrics.value.getCueAt(it))
                }
                onInitializeFail = { viewModel.setAudioInitFailed(true) }
                onPrepared = { audioReady = true }

                setOnCompletionListener {
                    for (cue in cues) {
                        for (word in cue.words) {
                            if (getIconForWord(word.parent) != null) {
                                WordReviseViewModel.appendWord(ReviseWord(word))
                            }
                        }
                    }

                    cleanup()
                    navHostController.navigate(Screen.WordRevise.route)
                }
            }

            withContext(Dispatchers.IO) {
                val stream = getStreamFrom(song.id)

                if (stream == null) {
                    Log.e(TAG, "Stream url is null")
                    viewModel.setAudioInitFailed(true)
                } else {
                    player.initialize(stream)
                }
            }
        }

        coroutineScope.launch {
            requestHandler.apply {
                endpoint = preferences["api_endpoint"] ?: "10.0.2.2"

                onLyricsSucceed = {
                    viewModel.lyricsParse(it)
                    viewModel.setCues(viewModel.lyrics.value)

                    lyricsReady = true

                    viewModel.initParser(Language.JAPANESE)
                    requestHandler.getDictionary("kanji")
                }
                onDictionarySucceed = { dictionaryReady = true }

                init(context)
            }

            val filter = preferences["filter_romanizations"] ?: true
            viewModel.setFilterRomanizations(filter)

            withContext(Dispatchers.IO) {
                val url = getSubtitleFor(song.id, "ja")

                if (!url.isNullOrEmpty()) {
                    requestHandler.getLyrics(url)
                } else {
                    requestHandler.getLyricsWordFind(song.searchQuery)
                }
            }
        }
    }

    OneTimeEffect {
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        systemUiController.isSystemBarsVisible = false
        systemUiController.systemBarsBehavior = 2

        setup()
    }

    LaunchedEffect(audioReady, lyricsReady, dictionaryReady) {
        if (audioReady && lyricsReady && dictionaryReady && !audioInitFailed && autoplay)
            player.togglePlay()
    }

    BackHandler {
        cleanup()
        navHostController.goBack()
    }

    Scaffold {
        if (audioInitFailed) {
            ErrorScreen({ cleanup() }, navHostController)
        } else {
            AsyncComposable(condition = (audioReady && lyricsReady && dictionaryReady)) {
                Box(Modifier.fillMaxSize().testTag("player-interface")) {
                    AsyncImage(
                        model = song.cover,
                        contentDescription = "${song.title} cover",
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
                        TextCue(modifier = Modifier.zIndex(1f), cue = currentCue)
                    }
                    // Box Controls overlay
                    FadeOutBox(duration = 250, stagnationTime = 3000) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
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
                                    text = song.title,
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
        }
    }
}

@Composable
private fun ErrorScreen(cleanup: () -> Unit, navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("error-screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(180.dp),
            painter = painterResource(id = R.drawable.radio),
            contentDescription = null
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "An error has occurred \nand the audio could not be played.",
            textAlign = TextAlign.Center,
            style = Typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.size(15.dp))
        Button(modifier =  Modifier.testTag("error-back-button"), onClick = { cleanup(); navHostController.goBack() }) {
            Text(text = "Go back to the home screen")
        }
    }
}