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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.gengolex.word.Word
import coil.compose.AsyncImage

@Composable
fun IdentifiedWord(word: Word, text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (word.representable) {
            val image = ImageCacheManager.getCachedImage(word.parent)

            if (image != null) AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                model = image,
                contentDescription = null
            )
        }
        Row {
            var i = 0

            while (i < word.word.length) {
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

                i++
            }
        }
    }
}