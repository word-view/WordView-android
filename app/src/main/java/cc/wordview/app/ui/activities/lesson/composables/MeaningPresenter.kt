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
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.R
import cc.wordview.app.components.ui.Space
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MeaningPresenter(
    lessonViewModel: LessonViewModel = hiltViewModel()
) {
    val currentWord by lessonViewModel.currentWord.collectAsStateWithLifecycle()
    val translations by lessonViewModel.translations.collectAsStateWithLifecycle()

    val langTag = AppSettings.language.get()
    val lang = remember { Language.byTag(langTag) }

    val screenWidthPx = with(LocalDensity.current) { 400.dp.toPx() }

    val offsetX = remember { Animatable(-screenWidthPx) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(currentWord) {
        lessonViewModel.playEffect(R.raw.discovery)
        offsetX.snapTo(-screenWidthPx)
        alpha.snapTo(0f)

        val slideIn = launch {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 600)
            )
        }
        val fadeIn = launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
        slideIn.join()
        fadeIn.join()

        val tokenWord = currentWord.tokenWord
        lessonViewModel.ttsSpeak(tokenWord.pronunciation ?: tokenWord.word, lang.locale)
        delay(2000)

        val slideOut = launch {
            offsetX.animateTo(
                targetValue = screenWidthPx,
                animationSpec = tween(durationMillis = 600)
            )
        }
        val fadeOut = launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 400)
            )
        }

        slideOut.join()
        fadeOut.join()

        lessonViewModel.postPresent()
    }

    fun getTranslated(): String {
        val parent = currentWord.tokenWord.parent

        var toReturn = parent

        for (translationEntry in translations) {
            if (translationEntry.parent == parent)
                toReturn = translationEntry.translation
        }

        return toReturn
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .testTag("meaning-presenter"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val image = ImageCacheManager.getCachedImage(currentWord.tokenWord.parent)
        AsyncImage(
            modifier = Modifier
                .size(130.dp)
                .alpha(alpha.value)
                .testTag("word-image"),
            model = image,
            contentDescription = null
        )
        Space(12.dp)
        Text(
            modifier = Modifier.alpha(alpha.value).testTag("word"),
            text = currentWord.tokenWord.word,
            textAlign = TextAlign.Center,
            style = if (lang == Language.JAPANESE) Typography.displayLarge else Typography.displayMedium,
        )
        Text(
            modifier = Modifier.alpha(alpha.value).testTag("translated-word"),
            text = getTranslated(),
            textAlign = TextAlign.Center,
            style = Typography.bodyLarge,
        )
    }
}