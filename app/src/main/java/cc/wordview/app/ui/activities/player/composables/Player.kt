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

package cc.wordview.app.ui.activities.player.composables

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.SongViewModel
import cc.wordview.app.extensions.getCleanUploaderName
import cc.wordview.app.ui.activities.player.viewmodel.PlayerViewModel
import cc.wordview.app.ui.components.CircularProgressIndicator
import cc.wordview.app.ui.components.FadeInAsyncImage
import cc.wordview.app.ui.components.FadeOutBox
import cc.wordview.app.ui.components.NotEnoughWordsDialog
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.components.PlayerButton
import cc.wordview.app.ui.components.PlayerTopBar
import cc.wordview.app.ui.components.Seekbar
import cc.wordview.app.ui.components.TextCue
import cc.wordview.app.ui.components.WordsPresentDialog
import cc.wordview.gengolex.word.Word

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Player(viewModel: PlayerViewModel, innerPadding: PaddingValues) {
    val player by viewModel.player.collectAsStateWithLifecycle()
    val currentCue by viewModel.currentCue.collectAsStateWithLifecycle()
    val cues by viewModel.cues.collectAsStateWithLifecycle()
    val playIcon by viewModel.playIcon.collectAsStateWithLifecycle()
    val finalized by viewModel.finalized.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isBuffering.collectAsStateWithLifecycle()
    val notEnoughWords by viewModel.notEnoughWords.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val bufferedPercentage by viewModel.bufferedPercentage.collectAsStateWithLifecycle()

    val videoStream by SongViewModel.videoStream.collectAsStateWithLifecycle()

    val activity = LocalActivity.current!!

    val composerMode = AppSettings.composerMode.get()
    var wordsPresentDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(finalized) {
        if (finalized) {
            player.stop()
            // TODO: Exit the activity for now
            activity.finish()
        }
    }

    fun back() {
        player.stop()
        activity.finish()
    }

    BackHandler { back() }

    if (notEnoughWords) NotEnoughWordsDialog { back() }

    if (wordsPresentDialog) {
        val words = ArrayList<Word>()

        for (cue in cues) words.addAll(cue.words)

        WordsPresentDialog({ wordsPresentDialog = false; player.play() }, words.distinct())
    }

    OneTimeEffect { wordsPresentDialog = true }

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

        FadeOutBox(
            duration = 250,
            stagnationTime = if (composerMode) 5000 * 10 else 5000
        ) {
            PlayerTopBar {
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