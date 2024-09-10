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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.app.subtitle.getIconForWord
import cc.wordview.app.subtitle.initializeIcons

@Composable
fun TextCue(cue: WordViewCue, modifier: Modifier = Modifier) {
    initializeIcons()

    Column(Modifier.wrapContentWidth(Alignment.Start)) {
        Row(
            modifier = modifier.fillMaxHeight(),
            verticalAlignment = Alignment.Bottom
        ) {
            val text = cue.text
            var currentIndex = 0

            while (currentIndex < text.length) {
                var foundWord = false

                for (word in cue.words) {
                    if (text.startsWith(word.word, currentIndex)) {
                        Column(modifier = Modifier.width(IntrinsicSize.Max), horizontalAlignment = Alignment.CenterHorizontally) {
                            getIconForWord(word.parent)?.let {
                                Icon(
                                    painter = it,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.inverseSurface
                                )
                            }
                            Text(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                text = word.word,
                                fontSize = 32.sp,
                                color = MaterialTheme.colorScheme.inverseSurface
                            )
                        }
                        currentIndex += word.word.length
                        foundWord = true
                        break
                    }
                }

                if (!foundWord) {
                    Text(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceDim),
                        text = text[currentIndex].toString(),
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                    currentIndex++
                }
            }
        }
    }
}