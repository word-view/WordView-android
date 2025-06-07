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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.gengolex.word.Word
import coil3.compose.AsyncImage

@Composable
fun IdentifiedWord(word: Word, text: String, modifier: Modifier = Modifier) {
    var textRowWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (word.representable) {
            val image = ImageCacheManager.getCachedImage(word.parent)
            if (image != null) {
                val imageSize =
                    if (textRowWidthPx > 0) with(density) { textRowWidthPx.toDp() }
                    else 48.dp
                AsyncImage(
                    modifier = Modifier.size(imageSize),
                    model = image,
                    contentDescription = null
                )
            }
        }
        Row(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    textRowWidthPx = coordinates.size.width
                }
        ) {
            for (i in word.word.indices) {
                val char = word.word[i]
                Text(
                    modifier = Modifier
                        .testTag("text-cue-plain")
                        .background(
                            if (word.representable)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ),
                    text = char.toString(),
                    fontSize = getFontSize(text),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    }
}