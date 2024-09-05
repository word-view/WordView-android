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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.subtitle.getIconForWord
import cc.wordview.app.subtitle.initializeIcons
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.languages.Word
import java.lang.Thread.sleep
import kotlin.concurrent.thread

@Composable
fun Presenter(current: Word, viewModel: WordReviseViewModel = WordReviseViewModel) {
    initializeIcons()

    val iconPainter = getIconForWord(current.parent)!!

    var visible by remember { mutableStateOf(false) }

    val answerStatus by viewModel.answerStatus.collectAsStateWithLifecycle()

    val scaleIn = animateFloatAsState(
        if (visible) 1f else 0f,
        tween(500, easing = EaseInOutExpo),
        label = "WordPresenterAnimation",
        finishedListener = {
            thread {
                // show if the user answered correctly
                sleep(1500)
                visible = false
                sleep(500) // wait for the scale out animation to end

                if (answerStatus != Answer.NONE) {
                    // show the right alternative
                    visible = true
                    viewModel.setAnswer(Answer.NONE)
                    sleep(3000)
                    visible = false
                    sleep(500)

                    viewModel.nextWord()
                    viewModel.setScreen(ReviseScreen.DragAndDrop.route)
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .scale(scaleIn.value)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (answerStatus) {
            Answer.CORRECT -> {
                Icon(
                    modifier = Modifier.size(130.dp),
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Correct"
                )
            }

            Answer.WRONG -> {
                Icon(
                    modifier = Modifier.size(130.dp),
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Wrong"
                )
            }

            Answer.NONE -> {
                Image(
                    modifier = Modifier.size(130.dp),
                    painter = iconPainter,
                    contentDescription = current.word
                )
                Text(
                    text = current.word,
                    textAlign = TextAlign.Center,
                    style = Typography.displayMedium,
                )
            }
        }
    }
}
