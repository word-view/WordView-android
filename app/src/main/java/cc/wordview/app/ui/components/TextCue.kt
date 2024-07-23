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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import cc.wordview.app.subtitle.WordViewCue

@Composable
fun TextCue(cue: WordViewCue, highlightedCuePosition: Int) {
    val highlighted = cue.startTimeMs == highlightedCuePosition

    val disabledCueColor = ColorUtils.blendARGB(
        MaterialTheme.colorScheme.inverseSurface.toArgb(),
        MaterialTheme.colorScheme.background.toArgb(),
        0.4f
    )

    val cueColor =
        if (highlighted) MaterialTheme.colorScheme.inverseSurface
        else Color(disabledCueColor)

    Spacer(modifier = Modifier.size(24.dp))
    Text(text = cue.text, fontSize = 24.sp, color = cueColor)
    Spacer(modifier = Modifier.size(24.dp))
}
