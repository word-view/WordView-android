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

package cc.wordview.app.ui.screens.lesson

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.ui.components.BackTopAppBar
import cc.wordview.app.ui.components.LessonQuitDialog
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.screens.components.Screen
import cc.wordview.app.ui.screens.results.ReviseResultsViewModel
import cc.wordview.app.ui.screens.lesson.components.ReviseScreen
import cc.wordview.app.ui.screens.lesson.components.ReviseTimer

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun Lesson(
    navHostController: NavHostController,
    viewModel: LessonViewModel = LessonViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val timerFinished by viewModel.timerFinished.collectAsStateWithLifecycle()
    val timer by viewModel.timer.collectAsStateWithLifecycle()

    val activity = LocalContext.current as Activity

    var openQuitConfirm by remember { mutableStateOf(false) }

    OneTimeEffect {
        viewModel.nextWord()
        ReviseTimer.start()
    }

    LaunchedEffect(timerFinished) {
        if (timerFinished) {
            ReviseResultsViewModel.setWords(viewModel.wordsToRevise.value)
            navHostController.navigate(Screen.ReviseResults.route)
        }
    }

    DisposableEffect(Unit) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

        onDispose {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    fun leave() {
        ReviseTimer.pause()
        LessonViewModel.cleanWords()
        navHostController.navigate(Screen.Home.route)
    }

    if (openQuitConfirm) {
        ReviseTimer.pause()
        LessonQuitDialog(
            onDismiss = {
                openQuitConfirm = false
                ReviseTimer.start()
            },
            onConfirm = {
                // if we don't close here it will appear at the home for a brief period
                openQuitConfirm = false
                leave()
            }
        )
    }

    Scaffold(topBar = {
        BackHandler { openQuitConfirm = true }
        BackTopAppBar(title = {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = timer, fontSize = 20.sp)
                Icon(
                    modifier = Modifier.padding(end = 12.dp, start = 6.dp),
                    imageVector = Icons.Filled.Timelapse,
                    contentDescription = "timer"
                )
            }
        }) { openQuitConfirm = true }
    }) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            label = "Screen switch cross fade",
            animationSpec = tween(250)
        ) {
            Box(Modifier.fillMaxSize()) {
                ReviseScreen.getByRoute(it)?.Composable(navHostController, innerPadding)
            }
        }
    }
}