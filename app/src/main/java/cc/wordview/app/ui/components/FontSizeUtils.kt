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

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import cc.wordview.gengolex.Language

fun getFontSize(cueText: String, langTag: String): TextUnit {
    val language = Language.byTag(langTag)
    val cueSize = cueText.length

    return when (language) {
        Language.JAPANESE -> calculateFontSize(cueSize, maxSize = 44.sp, minSize = 32.sp, maxCueSize = 24)
        else -> calculateFontSize(cueSize, maxSize = 44.sp, minSize = 24.sp, maxCueSize = 60)
    }
}

fun calculateFontSize(cueSize: Int, maxSize: TextUnit, minSize: TextUnit, maxCueSize: Int): TextUnit {
    val maxSizeValue = maxSize.value
    val minSizeValue = minSize.value

    val sizeValue = if (cueSize <= maxCueSize) {
        maxSizeValue - ((maxSizeValue - minSizeValue) * (cueSize.toFloat() / maxCueSize))
    } else {
        minSizeValue
    }

    return sizeValue.coerceAtLeast(minSize.value).sp
}