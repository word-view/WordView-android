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
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun FlashingBall(modifier: Modifier = Modifier, color: Color? = null, delayTime: Long = 0, ballSize: Dp = 320.dp) {
    val primary = color ?: MaterialTheme.colorScheme.primary

    val flashAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(delayTime)

        flashAlpha.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                delayMillis = 500,
                durationMillis = 150,
            )
        )

        flashAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                delayMillis = 120,
                durationMillis = 300,
            )
        )
    }

    Canvas(
        modifier = modifier.size(size = ballSize).alpha(flashAlpha.value)
    ) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to primary,
                    1.0f to primary,
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
    }
}