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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.extensions.detectTapGestures
import cc.wordview.app.extensions.dragGestures
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.activities.lesson.LessonNav
import cc.wordview.app.ui.activities.lesson.viewmodel.Answer
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.word.Word
import coil3.compose.AsyncImage
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

    val scale by animateFloatAsState(
        targetValue = if (isDragging || isPressed) 0.6f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    val coroutineScope = rememberCoroutineScope()

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDragging) offsetX else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isDragging) offsetY else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
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
            delay(500)

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
                DragMode.ICON -> TextItem(word = it, testTag = "top-word")
                DragMode.WORD -> IconItem(word = it, testTag = "top-word")
            }
        }

        Box(
            Modifier
                .offset {
                    IntOffset(animatedOffsetX.roundToInt(), animatedOffsetY.roundToInt())
                }
                .detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
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
                .scale(scale)
                .zIndex(10f)
                .testTag("drag")
        ) {
            currentWord.tokenWord.let {
                when (dragMode) {
                    DragMode.ICON -> IconItem(word = it, testTag = "current")
                    DragMode.WORD -> TextItem(word = it, testTag = "current")
                }
            }
        }

        downWord?.let {
            when (dragMode) {
                DragMode.ICON -> TextItem(word = it, testTag = "down-word")
                DragMode.WORD -> IconItem(word = it, testTag = "down-word")
            }
        }
    }
}

@Composable
fun TextItem(word: Word, testTag: String) {
    val langTag = AppSettings.language.get()
    val lang = remember { Language.byTag(langTag) }

    Text(
        modifier = Modifier.testTag(testTag),
        text = word.word,
        textAlign = TextAlign.Center,
        style = if (lang == Language.JAPANESE) Typography.displayLarge else Typography.displayMedium,
    )
}

@Composable
fun IconItem(word: Word, testTag: String) {
    val image = ImageCacheManager.getCachedImage(word.parent)

    AsyncImage(
        modifier = Modifier
            .size(130.dp)
            .testTag(testTag),
        model = image,
        contentDescription = null
    )
}