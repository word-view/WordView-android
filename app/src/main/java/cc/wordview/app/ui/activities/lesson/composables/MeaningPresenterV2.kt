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

import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.wordview.app.R
import cc.wordview.app.components.ui.BackTopAppBar
import cc.wordview.app.components.ui.Space
import cc.wordview.app.ui.components.FlashingBall
import cc.wordview.app.ui.components.InstantAnimatedVisibility
import cc.wordview.app.ui.components.TypeText
import cc.wordview.app.ui.theme.Typography
import cc.wordview.app.ui.theme.WordViewTheme
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import timber.log.Timber

@Composable
@Preview
fun MeaningPresenterV2() {
    val windowInfo = LocalWindowInfo.current
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val rotateDirection = remember { if (listOf(true, false).random()) -360f else 360f }

    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.discovery)
    }

    fun play() {
        mediaPlayer.seekTo(0)
        mediaPlayer.start()
    }

    LaunchedEffect(Unit) {
        scale.snapTo(1f)
        rotation.snapTo(0f)
        alpha.snapTo(1f)

        delay((200 * "World".count()).toLong())
        play()

        delay(2000L)

        scope.launch {
            scale.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1200)
            )
        }
        scope.launch {
            rotation.animateTo(
                targetValue = rotateDirection,
                animationSpec = tween(durationMillis = 1000)
            )
        }
        val alphaAnim = scope.launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 750)
            )
        }

        // if the user cant see anything anymore we are safe to
        // proceed without causing any visual glitches
        alphaAnim.invokeOnCompletion { Timber.i("Complete!") }
    }

    ProvidePreferenceLocals {
        WordViewTheme(darkTheme = true) {
            Scaffold(topBar = { BackTopAppBar(title = {}) {} }) { innerPadding ->
                Box(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
                    .width((windowInfo.containerSize.width * 2).dp)
                    .blur(300.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    FlashingBall(
                        color = Color(0xFFFFFF00),
                        delayTime = (100 * "World".count()).toLong()
                    )
                }

                Box(
                    modifier = Modifier
                        .padding()
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                            rotationZ = rotation.value
                        }
                        .alpha(alpha.value)
                        .testTag("meaning-presenter"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
//                        val image = ImageCacheManager.getCachedImage(currentWord.tokenWord.parent)
                        AsyncImage(
                            modifier = Modifier
                                .size(260.dp)
                                .testTag("word-image"),
                            model = "https://api.wordview.cc/api/v1/image?parent=world",
                            contentDescription = null
                        )
                        Space(12.dp)
                        TypeText(
                            text = "World",
                            textAlign = TextAlign.Center,
                            style = Typography.displayLarge,
                        )
                        // each letter takes 100ms to be "typed"
                        InstantAnimatedVisibility(delayTime = (200 * "World".count()).toLong()) {
                            Text(
                                modifier = Modifier.testTag("translated-word"),
                                text = "Mundo",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                style = Typography.headlineMedium,
                                fontSize = 24.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}