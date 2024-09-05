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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.extensions.goBack
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel
import cc.wordview.app.ui.screens.home.revise.ReviseScreen
import cc.wordview.app.ui.screens.home.revise.ReviseTimer
import cc.wordview.gengolex.languages.Word

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WordRevise(
    navHostController: NavHostController,
    viewModel: WordReviseViewModel = WordReviseViewModel
) {
    val current by viewModel.currentWord.collectAsStateWithLifecycle()
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    val formattedTime by viewModel.formattedTime.collectAsStateWithLifecycle()

    fun leave() {
        navHostController.goBack()
    }

    LaunchedEffect(Unit) {
        viewModel.setWordsToRevise(listOf(
            Word("night", "夜"),
            Word("voice", "声"),
            Word("cloud", "雲"),
            Word("umbrella", "傘"),
            Word("rain", "雨"),
        ))
        viewModel.setWord(viewModel.wordsToRevise.value.random())
        viewModel.setScreen(ReviseScreen.DragAndDrop.route)
    }

    DisposableEffect(Unit) {
        ReviseTimer.start()
        onDispose { ReviseTimer.pause() }
    }

    Scaffold(topBar = {
        BackHandler { leave() }
        TopAppBar(title = {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Timelapse, contentDescription = "timer")
                Text(text = formattedTime)
            }
        }, navigationIcon = {
            IconButton(onClick = { leave() }, modifier = Modifier.testTag("back-button")) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        })
    }) {
        Box(modifier = Modifier.fillMaxSize()) {
            ReviseScreen.getByRoute(screen)?.Composable(current)
        }
    }
}