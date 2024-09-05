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
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.subtitle.getIconForWord
import cc.wordview.app.subtitle.initializeIcons
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.languages.Word
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.math.roundToInt

@Composable
fun DragAndDrop(current: Word, viewModel: WordReviseViewModel = WordReviseViewModel) {
    initializeIcons()

    val words by viewModel.wordsToRevise.collectAsStateWithLifecycle()

    val iconPainter = getIconForWord(current.parent)!!

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var isDragging by remember { mutableStateOf(false) }

    var topWord by remember { mutableStateOf<Word?>(null) }
    var downWord by remember { mutableStateOf<Word?>(null) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDragging) offsetX else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = ""
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isDragging) offsetY else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = ""
    )

    fun correct() {
        thread {
            viewModel.setAnswer(Answer.CORRECT)
            sleep(500)
            viewModel.setScreen(ReviseScreen.Presenter.route)
        }
    }

    fun wrong() {
        thread {
            viewModel.setAnswer(Answer.WRONG)
            sleep(500)
            viewModel.setScreen(ReviseScreen.Presenter.route)
        }
    }

    fun handleDrop(x: Float, y: Float) {
        if (y < -450) {
            if (current == topWord) correct()
            else wrong()
        }

        if (y > 450) {
            if (current == downWord) correct()
            else wrong()
        }
    }

    LaunchedEffect(Unit) {
        val wordsOfLesson = listOf(current, words.filter { w -> w.word != current.word }.random()).shuffled()

        topWord = wordsOfLesson.first()
        downWord = wordsOfLesson.last()
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
        Box(modifier = Modifier
            .offset {
                IntOffset(
                    animatedOffsetX.roundToInt(),
                    animatedOffsetY.roundToInt()
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false

                        handleDrop(offsetX, offsetY)

                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    })
            }
        ) {
            Image(
                modifier = Modifier.size(130.dp),
                painter = iconPainter,
                contentDescription = current.word
            )
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