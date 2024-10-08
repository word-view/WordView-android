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

package cc.wordview.app.ui.screens.home.results

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.subtitle.getIconForWord
import cc.wordview.app.subtitle.initializeIcons
import cc.wordview.app.ui.components.BackTopAppBar
import cc.wordview.app.ui.screens.home.revise.components.ReviseWord
import cc.wordview.app.ui.screens.util.Screen
import cc.wordview.app.ui.theme.Typography

@Composable
fun ReviseResults(
    navHostController: NavHostController,
    viewModel: ReviseResultsViewModel = ReviseResultsViewModel
) {
    initializeIcons()

    val words by viewModel.words.collectAsStateWithLifecycle()

    fun leave() {
        viewModel.reset()
        navHostController.navigate(Screen.Home.route)
    }

    fun calculateCorrectnessRate(results: List<ReviseWord>): Int {
        if (results.isEmpty()) return 0
        val totalCorrects = results.sumOf { it.corrects }
        val totalAttempts = results.sumOf { it.corrects + it.misses }

        return if (totalAttempts == 0) 0 else ((totalCorrects.toDouble() / totalAttempts) * 100).toInt()
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
            Column(
                Modifier
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Words revised", style = Typography.headlineMedium)
                Text(
                    text = "${calculateCorrectnessRate(words)}% answer precision",
                    style = Typography.titleMedium
                )

                Spacer(Modifier.size(12.dp))

                for (word in words) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        getIconForWord(word.word.parent)?.let {
                            Image(
                                modifier = Modifier.size(80.dp),
                                painter = it,
                                contentDescription = null
                            )
                        }

                        Spacer(Modifier.size(12.dp))

                        Column {
                            Text(text = word.word.word, style = Typography.titleLarge)
                            Spacer(Modifier.size(4.dp))
                            Text(text = "Correct: ${word.corrects}", style = Typography.titleSmall)
                            Text(text = "Misses: ${word.misses}", style = Typography.titleSmall)
                        }
                    }
                    Spacer(Modifier.size(24.dp))
                }
            }
        }
    }
}