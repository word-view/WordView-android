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

package cc.wordview.app.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavHostController
import cc.wordview.app.api.APICallback
import cc.wordview.app.api.apiURL
import cc.wordview.app.api.getLyrics
import cc.wordview.app.currentSong
import cc.wordview.app.extensions.goBack
import cc.wordview.app.subtitle.SubtitleManager
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.app.ui.components.BackTopAppBar
import cc.wordview.app.ui.components.WVIconButton
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.util.AudioPlayer
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("MutableCollectionMutableState")
@Composable
fun Player(navController: NavHostController) {
    val context = LocalContext.current
    val subtitleManager = SubtitleManager()

    var cues by remember { mutableStateOf(ArrayList<WordViewCue>()) }
    var highlightedCuePosition by remember { mutableIntStateOf(0) }
    var playButtonIcon by remember { mutableStateOf(Icons.Filled.Pause) }

    val callback = object : APICallback {
        override fun onSuccessResponse(response: String?) {
            if (response != null) {
                subtitleManager.parseCues(response)
                cues = subtitleManager.cues
            }
        }

        override fun onErrorResponse(response: String?) {
            TODO("Not yet implemented")
        }
    }

    LaunchedEffect(Unit) {
        getLyrics(currentSong.id, "ja", callback, context)

        AudioPlayer.initialize("$apiURL/music/download?id=${currentSong.id}")
        AudioPlayer.prepare()
        AudioPlayer.start()
        AudioPlayer.addOnPositionChange { position ->
            val cue = subtitleManager.getCueAt(position)

            highlightedCuePosition = if (cue.startTimeMs != -1) cue.startTimeMs else 0
        }
        AudioPlayer.checkOnPositionChange()
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        BackTopAppBar(text = currentSong.title, onClickBack = {
            navController.goBack()
            AudioPlayer.stop()
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val disabledCueColor = ColorUtils.blendARGB(
                        MaterialTheme.colorScheme.inverseSurface.toArgb(),
                        MaterialTheme.colorScheme.background.toArgb(),
                        0.4f
                    )

                    for (cue in cues) {
                        val cueColor = if (cue.startTimeMs == highlightedCuePosition) MaterialTheme.colorScheme.inverseSurface else Color(disabledCueColor)

                        Text(text = cue.text, fontSize = 24.sp, color = cueColor)
                        Spacer(modifier = Modifier.size(5.dp))
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
                    onClick = {
                        AudioPlayer.togglePlay(
                            { playButtonIcon = Icons.Filled.Pause },
                            { playButtonIcon = Icons.Filled.PlayArrow })
                    },
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
