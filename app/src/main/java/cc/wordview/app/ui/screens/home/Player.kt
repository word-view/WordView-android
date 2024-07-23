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
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.SongViewModel
import cc.wordview.app.api.ResponseHandler
import cc.wordview.app.api.apiURL
import cc.wordview.app.api.getLyrics
import cc.wordview.app.api.getLyricsWordFind
import cc.wordview.app.extensions.goBack
import cc.wordview.app.ui.components.AsyncComposable
import cc.wordview.app.ui.components.BackTopAppBar
import cc.wordview.app.ui.components.WVIconButton
import cc.wordview.app.ui.screens.util.KeepScreenOn
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.audio.AudioPlayer
import cc.wordview.app.subtitle.Lyrics
import cc.wordview.app.ui.components.TextCue
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import kotlin.concurrent.thread

@SuppressLint("MutableCollectionMutableState")
@Composable
fun Player(navController: NavHostController, viewModel: PlayerViewModel = PlayerViewModel) {
    val context = LocalContext.current
    val lyricsScrollState = rememberLazyListState()

    val currentSong by SongViewModel.video.collectAsStateWithLifecycle()

    val cues by viewModel.cues.collectAsStateWithLifecycle()
    val lyrics = Lyrics()

    var highlightedCuePosition by remember { mutableIntStateOf(0) }
    var playButtonIcon by remember { mutableStateOf(Icons.Filled.PlayArrow) }

    val wordFindHandler = ResponseHandler({ res ->
        lyrics.parse(res)
        viewModel.setCues(lyrics)
        AudioPlayer.togglePlay()
    }, { _ ->
        Toast.makeText(
            context, "Could not find any lyrics matching this song.", Toast.LENGTH_LONG
        ).show()
    })

    val handler = ResponseHandler({ res ->
        lyrics.parse(res)
        viewModel.setCues(lyrics)
        AudioPlayer.togglePlay()
    }, { _ ->
        Toast.makeText(
            context,
            "Could not find any lyrics on youtube, will try searching for other platforms (beware: this may produce inaccurate lyrics)",
            Toast.LENGTH_LONG
        ).show()
        getLyricsWordFind(currentSong.searchQuery, wordFindHandler, context);
    })

    LaunchedEffect(Unit) {
        thread {
            AudioPlayer.initialize("$apiURL/music/download?id=${currentSong.id}")
            AudioPlayer.prepare()
            AudioPlayer.onPositionChange = { position ->
                val cue = lyrics.getCueAt(position)
                highlightedCuePosition = if (cue.startTimeMs != -1) cue.startTimeMs else 0
            }
            AudioPlayer.onPlay = { playButtonIcon = Icons.Filled.Pause }
            AudioPlayer.onPause = { playButtonIcon = Icons.Filled.PlayArrow }
            getLyrics(currentSong.id, "ja", handler, context)
        }
    }

    LaunchedEffect(highlightedCuePosition) {
        if (highlightedCuePosition != 0) {
            for (cue in cues) {
                if (cue.startTimeMs == highlightedCuePosition) {
                    // this offset is the sweet spot in the middle of the lyrics viewer
                    lyricsScrollState.animateScrollToItem(cues.indexOf(cue), -480)
                }
            }
        }
    }

    KeepScreenOn()
    BackHandler {
        AudioPlayer.stop()
        viewModel.clearCues()
        navController.goBack()
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        BackTopAppBar(text = currentSong.title, onClickBack = {
            AudioPlayer.stop()
            viewModel.clearCues()
            navController.goBack()
        })
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(500.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = DefaultRoundedCornerShape
            ) {
                AsyncComposable(
                    condition = (cues.size > 0),
                    modifier = Modifier.testTag("lyrics-loader"),
                    surface = true
                ) {
                    LazyColumn(
                        userScrollEnabled = false,
                        state = lyricsScrollState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp)
                            .height(500.dp)
                    ) {
                        for (cue in cues) {
                            item {
                                TextCue(
                                    cue = cue,
                                    highlightedCuePosition = highlightedCuePosition,
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WVIconButton(
                    onClick = { AudioPlayer.skipBackward() },
                    imageVector = Icons.Filled.SkipPrevious,
                    size = 75.dp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                )
                Spacer(modifier = Modifier.size(10.dp))
                WVIconButton(
                    onClick = { AudioPlayer.togglePlay() },
                    imageVector = playButtonIcon,
                    size = 80.dp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                )
                Spacer(modifier = Modifier.size(10.dp))
                WVIconButton(
                    onClick = { AudioPlayer.skipForward() },
                    imageVector = Icons.Filled.SkipNext,
                    size = 75.dp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                )
            }
        }
    }
}
