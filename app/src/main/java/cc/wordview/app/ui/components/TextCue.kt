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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import cc.wordview.app.subtitle.WordViewCue

@Composable
fun TextCue(cue: WordViewCue, modifier: Modifier = Modifier) {
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
                    if (word.parent == "") continue

                    if (text.lowercase().startsWith(word.word, currentIndex)) {
                        ImageRepresentableWord(
                            modifier = Modifier.padding(horizontal = 2.dp),
                            word = word,
                            currentIndex = currentIndex,
                            text = text,
                        )
                        currentIndex += word.word.length
                        foundWord = true
                        break
                    }
                }

                if (!foundWord) {
                    Text(
                        modifier = Modifier
                            .testTag("text-cue-word"),
                        text = text[currentIndex].toString(),
                        fontSize = getFontSize(text),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                    currentIndex++
                }
            }
        }
    }
}