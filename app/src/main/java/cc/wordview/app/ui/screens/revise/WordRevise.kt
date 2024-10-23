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

package cc.wordview.app.ui.screens.revise

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.ui.components.BackTopAppBar
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.screens.results.ReviseResultsViewModel
import cc.wordview.app.ui.screens.revise.components.ReviseScreen
import cc.wordview.app.ui.screens.revise.components.ReviseTimer
import cc.wordview.app.ui.screens.components.Screen

@SuppressLint("SourceLockedOrientationActivity", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WordRevise(
    navHostController: NavHostController,
) {
    val timerFinished by WordReviseViewModel.timerFinished.collectAsStateWithLifecycle()
    val lessonTime by WordReviseViewModel.formattedTime.collectAsStateWithLifecycle()
    val currentScreen by WordReviseViewModel.screen.collectAsStateWithLifecycle()

    val activity = LocalContext.current as Activity

    OneTimeEffect {
        WordReviseViewModel.nextWord()
        WordReviseViewModel.setScreen(ReviseScreen.getRandomScreen().route)
        ReviseTimer.start()
    }

    LaunchedEffect(timerFinished) {
        if (timerFinished) {
            ReviseResultsViewModel.setWords(WordReviseViewModel.wordsToRevise.value)
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
        WordReviseViewModel.cleanWords()
        navHostController.navigate(Screen.Home.route)
    }

    Scaffold(topBar = {
        BackHandler { leave() }
        BackTopAppBar(title = {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = lessonTime)
                Spacer(Modifier.size(4.dp))
                Icon(imageVector = Icons.Filled.Timelapse, contentDescription = "timer")
            }
        }) { leave() }
    }) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            ReviseScreen.getByRoute(currentScreen)?.Composable(navHostController, innerPadding)
        }
    }
}