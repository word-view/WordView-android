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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.components.ui.CircularProgressIndicator
import cc.wordview.app.components.ui.CrossfadeIconButton
import cc.wordview.app.components.ui.FadeInAsyncImage
import cc.wordview.app.components.ui.FadeOutBox
import cc.wordview.app.extensions.getCleanUploaderName
import cc.wordview.app.ui.activities.player.viewmodel.PlayerViewModel
import cc.wordview.app.components.ui.OneTimeEffect
import cc.wordview.app.components.ui.PlayerTopBar
import cc.wordview.app.components.ui.Seekbar
import cc.wordview.app.components.ui.findBiggerCutout
import cc.wordview.app.misc.PlayerSettings
import cc.wordview.app.ui.components.TextCue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Player(videoId: String, viewModel: PlayerViewModel, innerPadding: PaddingValues) {
    val currentCue by viewModel.currentCue.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isBuffering.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val bufferedPercentage by viewModel.bufferedPercentage.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val activity = LocalActivity.current!!
    val density = LocalDensity.current

    val composerMode = AppSettings.composerMode.get()
    val playbackSpeed = PlayerSettings.playbackSpeed.get()
    val backgroundImage = PlayerSettings.backgroundImage.get()

    var captionsEnabled by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.finalized) {
        if (uiState.finalized) {
            uiState.player.stop()
        }
    }

    fun back() {
        uiState.player.stop()
        activity.finish()
    }

    BackHandler { back() }
    OneTimeEffect { uiState.player.togglePlay(playbackSpeed) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("interface")
    ) {
        FadeInAsyncImage(
            image = uiState.videoStream.getHQThumbnail(),
            enabled = backgroundImage,
        )

        if (showSettings) {
            PlayerSettingsBottomSheet(
                onDismissRequest = {
                    uiState.player.togglePlay(playbackSpeed)
                    showSettings = false
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            if (captionsEnabled) TextCue(
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
            modifier = Modifier
                .fillMaxSize()
                .testTag("fade-out-box"),
            disabled = composerMode,
            duration = 250,
            stagnationTime = 5000
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.25f)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
            )
            PlayerTopBar(
                modifier = Modifier
                    .padding(
                        start = findBiggerCutout(density).dp / 2,
                        end = findBiggerCutout(density).dp / 2,
                    ),
                contentLeft = {
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
                            text = uiState.videoStream.info.name,
                            fontSize = 18.sp
                        )
                        Text(
                            text = uiState.videoStream.info.getCleanUploaderName(),
                            fontSize = 12.sp
                        )
                    }
                },
                contentRight = {
                    IconButton(
                        onClick = { captionsEnabled = !captionsEnabled },
                        modifier = Modifier.testTag("cc-toggle-button"),
                    ) {
                        Icon(
                            imageVector = if (captionsEnabled) Icons.Filled.ClosedCaption else Icons.Filled.ClosedCaptionOff,
                            contentDescription = "Back"
                        )
                    }
                    IconButton(
                        onClick = {
                            uiState.player.pause()
                            showSettings = true
                        },
                        modifier = Modifier.testTag("settings-toggle-button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
            )
            Seekbar(
                modifier = Modifier
                    .padding(top = TopAppBarDefaults.TopAppBarExpandedHeight)
                    .padding(horizontal = 6.dp)
                    .padding(
                        start = findBiggerCutout(density).dp / 2,
                        end = findBiggerCutout(density).dp / 2,
                    ),
                displayAdvancedInformation = composerMode,
                currentPosition = currentPosition,
                duration = uiState.player.getDuration(),
                videoId = videoId,
                bufferingProgress = bufferedPercentage
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
                    CrossfadeIconButton(
                        modifier = Modifier.testTag("skip-back"),
                        icon = Icons.Filled.SkipPrevious,
                        size = 72.dp,
                        onClick = { uiState.player.skipBack() }
                    )
                    CrossfadeIconButton(
                        modifier = Modifier
                            .testTag("toggle-play")
                            .alpha(if (isBuffering) 0.0f else 1.0f),
                        icon = uiState.playIcon,
                        size = 80.dp,
                        onClick = { uiState.player.togglePlay() }
                    )
                    CrossfadeIconButton(
                        modifier = Modifier.testTag("skip-forward"),
                        icon = Icons.Filled.SkipNext,
                        size = 72.dp,
                        onClick = { uiState.player.skipForward() }
                    )
                }
            }
        }
    }
}