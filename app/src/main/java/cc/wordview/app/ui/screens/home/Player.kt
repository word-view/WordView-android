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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.R
import cc.wordview.app.SongViewModel
import cc.wordview.app.api.apiURL
import cc.wordview.app.api.handler.PlayerRequestHandler
import cc.wordview.app.audio.AudioPlayer
import cc.wordview.app.extensions.goBack
import cc.wordview.app.extractor.getSubtitleFor
import cc.wordview.app.ui.components.AsyncComposable
import cc.wordview.app.ui.components.BackTopAppBar
import cc.wordview.app.ui.components.PlayerButton
import cc.wordview.app.ui.components.TextCue
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import com.gigamole.composefadingedges.verticalFadingEdges
import kotlin.concurrent.thread

@Composable
fun Player(
    navHostController: NavHostController,
    viewModel: PlayerViewModel = PlayerViewModel,
    requestHandler: PlayerRequestHandler = PlayerRequestHandler,
    autoplay: Boolean = true
) {
    val song by SongViewModel.video.collectAsStateWithLifecycle()
    val cues by viewModel.cues.collectAsStateWithLifecycle()
    val lyrics by viewModel.lyrics.collectAsStateWithLifecycle()
    val highlightedCuePosition by viewModel.highlightedCuePosition.collectAsStateWithLifecycle()
    val playIcon by viewModel.playIcon.collectAsStateWithLifecycle()

    val cuesScroll = rememberLazyListState()

    val audioPlayer by remember { mutableStateOf(AudioPlayer()) }

    var controlsLocked by remember { mutableStateOf(true) }
    var audioInitFailed by remember { mutableStateOf(false) }

    val context = LocalContext.current

    requestHandler.apply {
        onLyricsSucceed = {
            controlsLocked = false

            if (autoplay) audioPlayer.togglePlay()
            requestHandler.getDictionary("kanji")
        }

        init(context)
    }

    audioPlayer.apply {
        onPositionChange = {
            val cue = lyrics.getCueAt(it)

            if (cue.startTimeMs != -1) viewModel.highlightCueAt(cue.startTimeMs)
            else viewModel.unhighlightCues()
        }

        onInitializeFail = {
            audioInitFailed = true
            controlsLocked = true
        }
    }

    fun leave() {
        audioPlayer.stop()
        viewModel.clearCues()
        navHostController.goBack()
    }

    fun initAudio() {
        audioPlayer.initialize("$apiURL/music/download?id=${song.id}")
    }

    fun fetchLyrics() {
        val url = getSubtitleFor(song.id, "ja")

        if (!url.isNullOrEmpty()) {
            requestHandler.getLyrics(url)
        } else {
            requestHandler.getLyricsWordFind(song.searchQuery)
        }
    }

    LaunchedEffect(Unit) {
        // This is only threaded to make clear for the user that the app is not frozen
        // while it is downloading the necessary resources.
        // TODO: thread doesn't seem right to be placed here, delegate it elsewhere.
        thread {
            viewModel.initParser(Language.JAPANESE)
            initAudio()
            fetchLyrics()
        }
    }

    LaunchedEffect(highlightedCuePosition) {
        if (highlightedCuePosition != 0) {
            for (cue in cues) {
                if (cue.startTimeMs == highlightedCuePosition) {
                    cuesScroll.animateScrollToItem(cues.indexOf(cue), -480)
                }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        BackHandler { leave() }
        BackTopAppBar(text = song.title, onClickBack = { leave() })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.70f)
            ) {
                if (audioInitFailed) {
                    Column(
                        modifier = Modifier.fillMaxSize().testTag("error-message"),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(180.dp),
                            painter = painterResource(id = R.drawable.radio),
                            contentDescription = ""
                        )
                        Spacer(Modifier.size(15.dp))
                        Text(
                            text = "An error has occurred \nand the audio could not be played.",
                            textAlign = TextAlign.Center,
                            style = Typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    AsyncComposable(condition = (cues.size > 0)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .height(500.dp)
                                .testTag("lyrics-viewer"),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = DefaultRoundedCornerShape
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(horizontal = 9.dp, vertical = 20.dp)
                                    .verticalFadingEdges(),
                                userScrollEnabled = false,
                                state = cuesScroll
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
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1F),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                PlayerButton(
                    modifier = Modifier.testTag("skip-back"),
                    icon = Icons.Filled.SkipPrevious,
                    enabled = !controlsLocked,
                    size = 75.dp
                ) {
                    audioPlayer.skipBackward()
                }

                PlayerButton(
                    modifier = Modifier.testTag("play"),
                    icon = playIcon,
                    enabled = !controlsLocked,
                    size = 80.dp
                ) {
                    audioPlayer.togglePlay()
                }

                PlayerButton(
                    modifier = Modifier.testTag("skip-forward"),
                    icon = Icons.Filled.SkipNext,
                    enabled = !controlsLocked,
                    size = 75.dp
                ) {
                    audioPlayer.skipForward()
                }
            }
        }
    }
}
