/*
 * Copyright (c) 2025 Arthur Araujo
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

package cc.wordview.app.ui.activities.lesson.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.extensions.detectTapGestures
import cc.wordview.app.extensions.dragGestures
import cc.wordview.app.ui.activities.lesson.LessonNav
import cc.wordview.app.ui.activities.lesson.viewmodel.Answer
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.gengolex.word.Word
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Drag(mode: DragMode? = null) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragMode by remember { mutableStateOf(DragMode.random()) }
    var isPressed by remember { mutableStateOf(false) }

    // Drop animation state
    var isDropAnimating by remember { mutableStateOf(false) }
    var isDropComplete by remember { mutableStateOf(false) }
    val dropAnimOffsetY = remember { Animatable(0f) }
    val dropAnimAlpha = remember { Animatable(1f) }

    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if ((isDragging || isPressed) && !isDropComplete) 0.85f else 1f,
        animationSpec = tween(durationMillis = 280)
    )
    val rotation by animateFloatAsState(
        targetValue = if (isDragging && !isDropComplete) offsetX.coerceIn(-200f, 200f) / 3f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
    )

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDragging && !isDropComplete) offsetX else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isDragging && !isDropComplete) offsetY else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
    )

    val dragVisibleAlpha by animateFloatAsState(
        targetValue = if (isDropComplete) 0f else if (isDropAnimating) dropAnimAlpha.value else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    val currentWord by LessonViewModel.currentWord.collectAsStateWithLifecycle()
    val words by LessonViewModel.wordsToRevise.collectAsStateWithLifecycle()

    var topWord by remember { mutableStateOf<Word?>(null) }
    var downWord by remember { mutableStateOf<Word?>(null) }

    OneTimeEffect {
        val filteredWords = words
            .filter { w -> w.tokenWord.word != currentWord.tokenWord.word }
            .filter { w -> w.tokenWord.representable }

        val alternatives = listOf(currentWord.tokenWord, filteredWords.random().tokenWord).shuffled()

        topWord = alternatives.first()
        downWord = alternatives.last()

        dragMode = mode ?: DragMode.random()
    }

    val alternativesAlpha by animateFloatAsState(
        targetValue = if (isDropComplete || isDropAnimating) 0.3f else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    fun correct() {
        LessonViewModel.setAnswer(Answer.CORRECT)
        currentWord.corrects++
    }

    fun wrong() {
        LessonViewModel.setAnswer(Answer.WRONG)
        currentWord.misses++
    }

    fun onDrop(y: Float) {
        coroutineScope.launch {
            val dropTarget = when {
                y < -450 -> -800f
                y > 450 -> 800f
                else -> null
            }
            if (dropTarget != null) {
                isDropAnimating = true
                isDropComplete = false
                // Animate the drag item flying away and fading out
                dropAnimOffsetY.snapTo(y)
                dropAnimAlpha.snapTo(1f)
                launch {
                    dropAnimOffsetY.animateTo(dropTarget, animationSpec = tween(durationMillis = 320))
                }
                launch {
                    dropAnimAlpha.animateTo(0f, animationSpec = tween(durationMillis = 220))
                }
                delay(350)
                isDropComplete = true
            }

            if (y < -450) {
                if (currentWord.tokenWord.parent == topWord?.parent) correct()
                else wrong()
                LessonViewModel.setScreen(LessonNav.Presenter.route)
            }

            if (y > 450) {
                if (currentWord.tokenWord.parent == downWord?.parent) correct()
                else wrong()
                LessonViewModel.setScreen(LessonNav.Presenter.route)
            }

            // Reset drop animation
            isDropAnimating = false
            dropAnimOffsetY.snapTo(0f)
            dropAnimAlpha.snapTo(1f)
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
            Box(Modifier.alpha(alternativesAlpha)) {
                when (dragMode) {
                    DragMode.ICON -> TextItem(word = it, testTag = "top-word")
                    DragMode.WORD -> IconItem(word = it, testTag = "top-word")
                }
            }
        }

        Box(
            Modifier
                .offset {
                    IntOffset(
                        (if (isDropAnimating) 0f else animatedOffsetX).roundToInt(),
                        (if (isDropAnimating) dropAnimOffsetY.value else animatedOffsetY).roundToInt()
                    )
                }
                .detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
                .dragGestures(
                    onDragStart = {
                        if (!isDropAnimating && !isDropComplete) isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        if (!isDropAnimating && !isDropComplete)
                            onDrop(offsetY)
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, dragAmount ->
                        if (!isDropAnimating && !isDropComplete) {
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    })
                .scale(scale)
                .alpha(dragVisibleAlpha)
                .zIndex(10f)
                .testTag("drag")
                .graphicsLayer {
                    rotationZ = rotation
                }
        ) {
            currentWord.tokenWord.let {
                when (dragMode) {
                    DragMode.ICON -> IconItem(word = it, testTag = "current")
                    DragMode.WORD -> TextItem(word = it, testTag = "current")
                }
            }
        }

        downWord?.let {
            Box(Modifier.alpha(alternativesAlpha)) {
                when (dragMode) {
                    DragMode.ICON -> TextItem(word = it, testTag = "down-word")
                    DragMode.WORD -> IconItem(word = it, testTag = "down-word")
                }
            }
        }
    }
}
