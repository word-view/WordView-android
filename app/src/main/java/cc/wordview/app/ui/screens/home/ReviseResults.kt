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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.ui.components.BackTopAppBar
import cc.wordview.app.ui.screens.home.model.ReviseResultsViewModel
import cc.wordview.app.ui.screens.util.Screen

@Composable
fun ReviseResults(navHostController: NavHostController, viewModel: ReviseResultsViewModel = ReviseResultsViewModel) {
    val words by viewModel.words.collectAsStateWithLifecycle()
    val answeredCorrectly by viewModel.answeredCorrectly.collectAsStateWithLifecycle()
    val answeredWrong by viewModel.answeredWrong.collectAsStateWithLifecycle()

    fun leave() {
        navHostController.navigate(Screen.Home.route)
    }

    Scaffold(topBar = {
        BackHandler { leave() }
        BackTopAppBar(title = {}) { leave() }
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column {
                Text(text = "Correct: $answeredCorrectly")
                Text(text = "Wrong: $answeredWrong")
            }
        }
    }
}