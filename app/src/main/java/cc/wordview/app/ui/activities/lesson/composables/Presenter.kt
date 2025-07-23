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

import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.R
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.activities.lesson.viewmodel.Answer
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun Presenter(
    lessonViewModel: LessonViewModel = hiltViewModel()
) {
    val answerStatus by lessonViewModel.answerStatus.collectAsStateWithLifecycle()
    val currentWord by lessonViewModel.currentWord.collectAsStateWithLifecycle()

    var visible by remember { mutableStateOf(false) }

    val langTag = AppSettings.language.get()
    val context = LocalContext.current

    val scaleIn = animateFloatAsState(
        if (visible) 1f else 0.01f,
        tween(500, easing = EaseInOutExpo),
        label = "WordPresenterAnimation",
    )

    val fadeInOut = animateFloatAsState(
        if (visible) 1f else 0f,
        tween(250, easing = EaseInOutExpo),
        label = "WordPresenterAlpha",
    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = scaleIn.value) {
        if (scaleIn.value == 1f) {
            scope.launch {
                lessonViewModel.playEffect(answerStatus)

                delay(1500.milliseconds)
                visible = false
                delay(500.milliseconds)

                if (answerStatus != Answer.NONE) {
                    val answerToNextWord = answerStatus

                    lessonViewModel.setAnswer(Answer.NONE)
                    visible = true

                    val tokenWord = currentWord.tokenWord

                    lessonViewModel.ttsSpeak(tokenWord.pronunciation ?: tokenWord.word, Language.byTag(langTag).locale)

                    delay(3000.milliseconds)
                    visible = false
                    delay(500.milliseconds)

                    lessonViewModel.nextWord(answerToNextWord)
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
                        .testTag("correct")
                        .alpha(fadeInOut.value),
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Correct"
                )
            }

            Answer.WRONG -> {
                Icon(
                    modifier = Modifier
                        .size(130.dp)
                        .testTag("wrong")
                        .alpha(fadeInOut.value),
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Wrong"
                )
            }

            Answer.NONE -> {
                val image = ImageCacheManager.getCachedImage(currentWord.tokenWord.parent)
                AsyncImage(
                    modifier = Modifier
                        .size(130.dp)
                        .testTag("word")
                        .alpha(fadeInOut.value),
                    model = image,
                    contentDescription = null
                )

                val lang = remember { Language.byTag(langTag) }

                Text(
                    text = currentWord.tokenWord.word,
                    textAlign = TextAlign.Center,
                    style = if (lang == Language.JAPANESE) Typography.displayLarge else Typography.displayMedium,
                    modifier = Modifier.alpha(fadeInOut.value)
                )
            }
        }
    }
}