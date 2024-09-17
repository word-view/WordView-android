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
import cc.wordview.app.ui.screens.home.model.ReviseResultsViewModel
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel
import cc.wordview.app.ui.screens.home.revise.ReviseScreen
import cc.wordview.app.ui.screens.home.revise.ReviseTimer
import cc.wordview.app.ui.screens.util.Screen

@SuppressLint("SourceLockedOrientationActivity", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WordRevise(
    navHostController: NavHostController,
    viewModel: WordReviseViewModel = WordReviseViewModel
) {
    val activity = (LocalContext.current as Activity)

    val currentScreen by viewModel.screen.collectAsStateWithLifecycle()
    val lessonTime by viewModel.formattedTime.collectAsStateWithLifecycle()
    val initialized by viewModel.initialized.collectAsStateWithLifecycle()

    fun leave() {
        ReviseTimer.pause()
        viewModel.cleanWords()
        navHostController.navigate(Screen.Home.route)
    }

    LaunchedEffect(Unit) {
        if (!initialized) {
            viewModel.initialize()
            ReviseResultsViewModel.setWords(viewModel.wordsToRevise.value)
            viewModel.nextWord()
            viewModel.setScreen(ReviseScreen.getRandomScreen().route)

            ReviseTimer.start { navHostController.navigate(Screen.ReviseResults.route) }
        }
    }

    DisposableEffect(Unit) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

        onDispose {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
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
                Spacer(Modifier.size(6.dp))
                Icon(imageVector = Icons.Filled.Timelapse, contentDescription = "timer")
            }
        }) { leave() }
    }) {
        Box(Modifier.fillMaxSize()) {
            ReviseScreen.getByRoute(currentScreen)?.Composable(navHostController)
        }
    }
}