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

package cc.wordview.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cc.wordview.app.R
import cc.wordview.app.components.extensions.fillMaxWidth
import cc.wordview.app.components.extensions.percentageOf
import cc.wordview.app.components.ui.AsyncImagePlaceholders
import cc.wordview.app.components.ui.Icon
import cc.wordview.app.components.ui.RemoteImage
import cc.wordview.app.components.ui.Space
import cc.wordview.app.database.entity.ViewedVideo
import cc.wordview.app.extensions.toMinutesSeconds

@Composable
fun ContinueWatchingCard(modifier: Modifier = Modifier, viewedVideo: ViewedVideo, onClick: () -> Unit = {}) {
    val targetWidth = viewedVideo.duration.percentageOf(viewedVideo.watchedUntil)
    val animatedWidth = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedWidth.animateTo(
            targetValue = targetWidth.toFloat(),
            animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier
            .height(192.dp)
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Box {
            Row(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(bottomEnd = 10.dp)
                    )
                    .padding(vertical = 3.dp, horizontal = 12.dp)
                    .width(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Replay, Modifier.size(16.dp))
                Space(12.dp)
                Text(
                    text = stringResource(R.string.continue_watching),
                    style = typography.labelMedium,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
            RemoteImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(-1f),
                contentScale = ContentScale.FillWidth,
                model = "https://i.ytimg.com/vi_webp/${viewedVideo.id}/maxresdefault.webp",
                asyncImagePlaceholders = AsyncImagePlaceholders(
                    noConnectionWhite = R.drawable.nonet,
                    noConnectionDark = R.drawable.nonet_dark
                )
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            endY = 320f,
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surfaceContainerHighest,
                            )
                        )
                    ),
            ) {
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 12.dp, end = 12.dp)
                ) {
                    Column {
                        Text(
                            text = viewedVideo.title,
                            style = typography.titleLarge,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            text = viewedVideo.artist,
                            style = typography.titleSmall,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }
                    Box(
                        Modifier
                            .padding(top = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                RoundedCornerShape(12.dp)
                            )
                            .width(IntrinsicSize.Max)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(4.dp)
                                .padding(horizontal = 8.dp),
                            text = "${viewedVideo.watchedUntil.toMinutesSeconds()} / ${viewedVideo.duration.toMinutesSeconds()}",
                            style = typography.labelSmall,
                            textAlign = TextAlign.Right,
                            fontSize = 14.sp
                        )
                    }
                }
                Box(
                    Modifier
                        .fillMaxWidth(animatedWidth.value.toInt())
                        .height(4.dp)
                        .background(color = MaterialTheme.colorScheme.primary)
                        .align(Alignment.BottomStart)
                )
            }
        }
    }
}