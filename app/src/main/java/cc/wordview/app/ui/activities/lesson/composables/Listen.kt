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

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.extensions.random
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.ui.activities.lesson.LessonNav
import cc.wordview.app.ui.activities.lesson.viewmodel.Answer
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.components.Icon
import cc.wordview.app.components.ui.OneTimeEffect
import cc.wordview.app.ui.components.Space
import cc.wordview.app.ui.components.WordButton
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.word.Word
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Listen(
    mode: LessonMode? = null,
    lessonViewModel: LessonViewModel = hiltViewModel()
) {
    val currentWord by lessonViewModel.currentWord.collectAsStateWithLifecycle()
    val words by lessonViewModel.wordsToRevise.collectAsStateWithLifecycle()

    val alternatives = remember { arrayListOf<Word>() }

    val langTag = AppSettings.language.get()
    val lang = remember { Language.byTag(langTag) }

    var mainText by remember { mutableStateOf("") }
    var revealedText by remember { mutableStateOf(false) }
    var buttonsEnabled by remember { mutableStateOf(true) }
    var canListen by remember { mutableStateOf(true) }
    var selectedWord by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    var lessonMode by remember { mutableStateOf(LessonMode.random()) }

    val coroutineScope = rememberCoroutineScope()

    OneTimeEffect {
        val filteredWords = words
            .filter { w -> w.tokenWord.word != currentWord.tokenWord.word }
            .filter { w -> w.tokenWord.representable }

        val res = filteredWords.map { it.tokenWord }.random(3) + currentWord.tokenWord

        alternatives.addAll(res.shuffled())

        val wordLength = currentWord.tokenWord.word.length
        mainText = "_".repeat(wordLength)

        lessonMode = mode ?: LessonMode.random()
    }

    fun correct() {
        lessonViewModel.setAnswer(Answer.CORRECT)
        currentWord.corrects++
    }

    fun wrong() {
        lessonViewModel.setAnswer(Answer.WRONG)
        currentWord.misses++
    }

    fun validate() {
        if (selectedWord?.word == currentWord.tokenWord.word) {
            correct()
            isCorrect = true
        } else {
            wrong()
            isCorrect = false
        }
        lessonViewModel.setScreen(LessonNav.Presenter.route)
    }

    // Animate main text reveal
    val mainTextScale by animateFloatAsState(
        targetValue = if (revealedText) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 350f),
        label = "mainTextScale"
    )
    val mainTextAlpha by animateFloatAsState(
        targetValue = if (revealedText) 1f else 0.85f,
        animationSpec = tween(durationMillis = 180),
        label = "mainTextAlpha"
    )

    // Animate WordCards fade/scale when disabled
    val wordCardAlpha: (Word) -> Float = { word ->
        if (buttonsEnabled) 1f
        else if (word == selectedWord) 1f
        else 0.3f
    }
    val wordCardScale: (Word) -> Float = { word ->
        if (buttonsEnabled) 1f
        else if (word == selectedWord) 1.10f
        else 0.95f
    }

    val resultScale by animateFloatAsState(
        targetValue = if (isCorrect != null) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "resultScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("listen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(256.dp)
                .padding(bottom = 12.dp)
        ) {
            if (!canListen) {
                val infiniteTransition = rememberInfiniteTransition(label = "waves")
                val wave1 by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(900, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "wave1"
                )
                val wave2 by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(900, 450, LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "wave2"
                )
                val color = MaterialTheme.colorScheme.primary

                Canvas(Modifier.matchParentSize()) {
                    // Outer wave 1
                    drawCircle(
                        color = color.copy(alpha = (1f - wave1) * 0.22f),
                        radius = size.minDimension / 2 * (0.7f + wave1 * 0.7f),
                        center = Offset(size.width / 2, size.height / 2)
                    )
                    // Outer wave 2
                    drawCircle(
                        color = color.copy(alpha = (1f - wave2) * 0.16f),
                        radius = size.minDimension / 2 * (0.7f + wave2 * 0.7f),
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }
            }

            Surface(
                modifier = Modifier.size(128.dp).testTag("listen-button"),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DefaultRoundedCornerShape,
                enabled = canListen,
                onClick = {
                    canListen = false

                    val toPronounce = currentWord.tokenWord.pronunciation ?: currentWord.tokenWord.word
                    lessonViewModel.ttsSpeak(toPronounce, lang.locale)

                    coroutineScope.launch {
                        delay(900)
                        canListen = true
                    }
                }
            ) {
                Icon(
                    modifier = Modifier.size(42.dp),
                    imageVector = Icons.Filled.VolumeUp
                )
            }
        }

        Text(
            text = if (revealedText && selectedWord != null) selectedWord!!.word else mainText,
            textAlign = TextAlign.Center,
            style = if (lang == Language.JAPANESE) Typography.displayLarge else Typography.displayMedium,
            modifier = Modifier
                .scale(if (isCorrect != null) resultScale else mainTextScale)
                .alpha(if (isCorrect != null) 1f else mainTextAlpha)
                .padding(bottom = 16.dp)
                .testTag("reveal-text"),
        )

        Column(
            modifier = Modifier.padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            for (word in alternatives) {
                WordButton(
                    text = {
                        when (lessonMode) {
                            LessonMode.WORD -> {
                                Text(
                                    text = word.word,
                                    style = if (lang == Language.JAPANESE) Typography.displayMedium else Typography.displaySmall,
                                    softWrap = false
                                )
                            }

                            LessonMode.ICON -> {
                                IconItem(
                                    word = word,
                                    size = 48.dp,
                                    testTag = "icon-item-alternative"
                                )
                            }
                        }
                    },
                    enabled = buttonsEnabled,
                    modifier = Modifier
                        .scale(wordCardScale(word))
                        .alpha(wordCardAlpha(word))
                        .testTag("alternative"),
                    onClick = {
                        buttonsEnabled = false
                        selectedWord = word
                        revealedText = true
                        // Delay for animation effect before validation
                        coroutineScope.launch {
                            delay(220)
                            validate()
                        }
                    }
                )
                Space(12.dp)
            }
        }
    }
}