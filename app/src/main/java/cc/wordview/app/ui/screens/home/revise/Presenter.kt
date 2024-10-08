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

import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.subtitle.getIconForWord
import cc.wordview.app.subtitle.initializeIcons
import cc.wordview.app.ui.screens.home.revise.components.Answer
import cc.wordview.app.ui.screens.home.revise.components.ReviseScreen
import cc.wordview.app.ui.theme.Typography
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun Presenter(viewModel: WordReviseViewModel = WordReviseViewModel) {
    initializeIcons()

    val answerStatus by viewModel.answerStatus.collectAsStateWithLifecycle()
    val current by viewModel.currentWord.collectAsStateWithLifecycle()

    val iconPainter = getIconForWord(current.word.parent)

    var visible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val scaleIn = animateFloatAsState(
        if (visible) 1f else 0.01f,
        tween(500, easing = EaseInOutExpo),
        label = "WordPresenterAnimation",
    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = scaleIn.value) {
        if (scaleIn.value == 1f) {
            scope.launch {
                delay(1500.milliseconds)
                visible = false
                delay(500.milliseconds)

                if (answerStatus != Answer.NONE) {
                    val answerToNextWord = answerStatus

                    viewModel.setAnswer(Answer.NONE)
                    visible = true

                    viewModel.ttsSpeak(context, current.word.word, Locale.JAPANESE)

                    delay(3000.milliseconds)
                    visible = false
                    delay(500.milliseconds)

                    viewModel.nextWord(answerToNextWord)
                    viewModel.setScreen(ReviseScreen.getRandomScreen().route)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .scale(scaleIn.value)
            .fillMaxSize()
            .testTag("root"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (answerStatus) {
            Answer.CORRECT -> {
                Icon(
                    modifier = Modifier
                        .size(130.dp)
                        .testTag("correct"),
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Correct"
                )
            }

            Answer.WRONG -> {
                Icon(
                    modifier = Modifier
                        .size(130.dp)
                        .testTag("wrong"),
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Wrong"
                )
            }

            Answer.NONE -> {
                iconPainter?.let {
                    Image(
                        modifier = Modifier
                            .size(130.dp)
                            .testTag("word"),
                        painter = iconPainter,
                        contentDescription = current.word.word
                    )
                }
                Text(
                    text = current.word.word,
                    textAlign = TextAlign.Center,
                    style = Typography.displayMedium,
                )
            }
        }
    }
}
