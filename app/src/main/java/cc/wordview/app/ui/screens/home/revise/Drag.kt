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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.extensions.dragGestures
import cc.wordview.app.subtitle.getIconForWord
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel
import cc.wordview.app.ui.screens.home.revise.components.Answer
import cc.wordview.app.ui.screens.home.revise.components.DragMode
import cc.wordview.app.ui.screens.home.revise.components.ReviseScreen
import cc.wordview.app.ui.screens.home.revise.model.DragViewModel
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.languages.Word
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Drag(
    navHostController: NavHostController,
    viewModel: WordReviseViewModel = WordReviseViewModel,
    dragViewModel: DragViewModel = DragViewModel,
    mode: DragMode? = null
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var isDragging by remember { mutableStateOf(false) }
    var dragMode by remember { mutableStateOf(DragMode.random()) }

    val coroutineScope = rememberCoroutineScope()

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDragging) offsetX else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isDragging) offsetY else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
    )

    val currentWord by viewModel.currentWord.collectAsStateWithLifecycle()
    val words by viewModel.wordsToRevise.collectAsStateWithLifecycle()
    val topWord by dragViewModel.topWord.collectAsStateWithLifecycle()
    val downWord by dragViewModel.downWord.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        val filteredWords = words.filter { w -> w.word.word != currentWord.word.word }

        val alternatives = listOf(currentWord.word, filteredWords.random().word).shuffled()

        dragViewModel.setTopWord(alternatives.first())
        dragViewModel.setDownWord(alternatives.last())

        dragMode = mode ?: DragMode.random()
    }

    fun correct() {
        viewModel.setAnswer(Answer.CORRECT)
        currentWord.corrects++
    }

    fun wrong() {
        viewModel.setAnswer(Answer.WRONG)
        currentWord.misses++
    }

    fun onDrop(y: Float) {
        coroutineScope.launch {
            delay(500)

            if (y < -450) {
                if (currentWord.word == topWord) correct()
                else wrong()

                viewModel.setScreen(ReviseScreen.Presenter.route)
            }

            if (y > 450) {
                if (currentWord.word == downWord) correct()
                else wrong()

                viewModel.setScreen(ReviseScreen.Presenter.route)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("root"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        topWord?.let {
            when (dragMode) {
                DragMode.ICON -> Text(word = it, testTag = "top-word")
                DragMode.WORD -> Icon(word = it, testTag = "top-word")
            }
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

                        onDrop(offsetY)

                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    })
                .testTag("drag")
        ) {
            currentWord.word.let {
                when (dragMode) {
                    DragMode.ICON -> Icon(word = it, testTag = "current")
                    DragMode.WORD -> Text(word = it, testTag = "current")
                }
            }
        }

        downWord?.let {
            when (dragMode) {
                DragMode.ICON -> Text(word = it, testTag = "down-word")
                DragMode.WORD -> Icon(word = it, testTag = "down-word")
            }
        }
    }
}

@Composable
fun Text(word: Word, testTag: String) {
    Text(
        modifier = Modifier.testTag(testTag),
        text = word.word,
        textAlign = TextAlign.Center,
        style = Typography.displayMedium,
    )
}

@Composable
fun Icon(word: Word, testTag: String) {
    getIconForWord(word.parent)?.let { icon ->
        Image(
            modifier = Modifier
                .size(130.dp)
                .testTag(testTag),
            painter = icon,
            contentDescription = word.word
        )
    }
}
