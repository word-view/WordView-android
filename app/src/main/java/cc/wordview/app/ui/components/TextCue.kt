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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            modifier = modifier.height(32.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            var previousWordsCount = 0
            var previousWord = ""

            for (word in cue.words) {
                val wordIndex = cue.text.indexOf(word.word)

                var iconMargin = 1

                if (wordIndex > 0) for (i in 0..<wordIndex) {
                    iconMargin += if (cue.text[i] == ' ' || cue.text[i] == '「' || cue.text[i] == '」') 15 else 32

                }

                if (previousWordsCount > 0) for (i in 1..previousWordsCount) {
                    for (o in previousWord.indices - 1) {
                        iconMargin -= 32
                    }
                }

                if (iconMargin < 0) iconMargin = 1

                getIconForWord(word.parent)?.let {
                    Icon(
                        painter = it,
                        modifier = Modifier
                            .padding(start = iconMargin.dp)
                            .size(32.dp),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.inverseSurface
                    )

                    previousWordsCount++
                    previousWord = word.word
                }
            }
        }
        Text(
            text = cue.text,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.inverseSurface,
            lineHeight = 32.sp
        )
        Spacer(Modifier.size(24.dp))
    }
}
