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

package cc.wordview.app.ui.screens.home.revise

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.extensions.dragGestures
import cc.wordview.app.subtitle.getIconForWord
import cc.wordview.app.ui.screens.home.model.ReviseResultsViewModel
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel
import cc.wordview.app.ui.screens.home.revise.model.DragAndDropViewModel
import cc.wordview.app.ui.screens.util.Screen
import cc.wordview.app.ui.theme.Typography
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.math.roundToInt

@Composable
fun IconDrag(
    navHostController: NavHostController,
    viewModel: WordReviseViewModel = WordReviseViewModel,
    dragViewModel: DragAndDropViewModel = DragAndDropViewModel
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var isDragging by remember { mutableStateOf(false) }

    val currentWord by viewModel.currentWord.collectAsStateWithLifecycle()
    val words by viewModel.wordsToRevise.collectAsStateWithLifecycle()
    val topWord by dragViewModel.topWord.collectAsStateWithLifecycle()
    val downWord by dragViewModel.downWord.collectAsStateWithLifecycle()

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDragging) offsetX else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isDragging) offsetY else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
    )

    LaunchedEffect(Unit) {
        val wordsWithoutCurrent = words.filter { w -> w.word != currentWord.word }

        if (wordsWithoutCurrent.isEmpty()) {
            viewModel.deInitialize()
            navHostController.navigate(Screen.ReviseResults.route)
        }

        val alternatives = listOf(currentWord, wordsWithoutCurrent.random()).shuffled()

        dragViewModel.setTopWord(alternatives.first())
        dragViewModel.setDownWord(alternatives.last())
    }

    fun correct() {
        viewModel.setAnswer(Answer.CORRECT)
        ReviseResultsViewModel.incrementCorrect()
    }

    fun wrong() {
        viewModel.setAnswer(Answer.WRONG)
        ReviseResultsViewModel.incrementWrong()
    }

    fun handleDrop(y: Float) {
        if (y < -450) {
            if (currentWord == topWord) correct()
            else wrong()
        }

        if (y > 450) {
            if (currentWord == downWord) correct()
            else wrong()
        }

        thread {
            sleep(500)
            viewModel.setScreen(ReviseScreen.Presenter.route)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        topWord?.let {
            Text(
                text = it.word,
                textAlign = TextAlign.Center,
                style = Typography.displayMedium,
            )
        }
        Box(
            Modifier
                .offset {
                    IntOffset(animatedOffsetX.roundToInt(), animatedOffsetY.roundToInt())
                }
                .dragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false

                        handleDrop(offsetY)

                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    })
        ) {
            getIconForWord(currentWord.parent)?.let {
                Image(
                    modifier = Modifier.size(130.dp),
                    painter = it,
                    contentDescription = currentWord.word
                )
            }
        }
        downWord?.let {
            Text(
                text = it.word,
                textAlign = TextAlign.Center,
                style = Typography.displayMedium,
            )
        }
    }
}