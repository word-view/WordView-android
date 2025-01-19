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

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun FadeInAsyncImage(image: Bitmap?) {
    var isVisible by rememberSaveable { mutableStateOf(false) }

    // This prevents the background from "flashing" due to recompositions (probably)
    val img = remember { image }

    LaunchedEffect(img) {
        isVisible = false
        isVisible = true
    }

    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 500,
                easing = EaseInExpo
            )
        )
    ) {
        AsyncImage(
            model = img,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.15f),
            contentScale = ContentScale.FillWidth
        )
    }
}