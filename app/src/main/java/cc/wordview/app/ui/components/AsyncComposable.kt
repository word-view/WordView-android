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

package cc.wordview.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils

@Composable
/**
 * A composable that renders a CircularProgressIndicator while a condition is not fulfilled yet.
 * The modifier parameter will only apply to the Box that encapsulates the CircularProgressIndicator.
 */
fun AsyncComposable(modifier: Modifier = Modifier, condition: Boolean, surface: Boolean = false, composable: @Composable () -> Unit) {
    if (!condition) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val inverseSurfaceDarker = ColorUtils.blendARGB(
                MaterialTheme.colorScheme.inverseSurface.toArgb(),
                MaterialTheme.colorScheme.background.toArgb(),
                0.4f
            )

            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = if (surface) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.secondary,
                trackColor = if (surface) Color(inverseSurfaceDarker) else MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    } else {
        composable()
    }
}