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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.extensions.getOrDefault
import cc.wordview.app.subtitle.WordViewCue
import coil.compose.AsyncImage
import me.zhanghai.compose.preference.LocalPreferenceFlow

@Composable
fun TextCue(cue: WordViewCue, modifier: Modifier = Modifier) {
    val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()
    val langtag = remember { preferences.getOrDefault<String>("language") }

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
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (word.representable) {
                                val image = GlobalImageLoader.getCachedImage(word.parent)
                                
                                if (image != null) AsyncImage(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f),
                                    model = image,
                                    contentDescription = null
                                )
                            }
                            Text(
                                modifier = Modifier
                                    .background(
                                        if (word.representable)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.secondaryContainer
                                    )
                                    .testTag("text-cue-plain"),
                                text = word.word,
                                fontSize = getFontSize(text, langtag),
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
                        modifier = Modifier.testTag("text-cue-word"),
                        text = text[currentIndex].toString(),
                        fontSize = getFontSize(text, langtag),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                    currentIndex++
                }
            }
        }
    }
}