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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.ImageCacheManager
import cc.wordview.app.extensions.capitalize
import cc.wordview.app.extensions.getOrDefault
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.gengolex.word.Representation
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
                            modifier = Modifier
                                .width(IntrinsicSize.Max)
                                .padding(horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (word.representable) {
                                when (word.representation) {
                                    Representation.ILLUSTRATION.name -> {
                                        val image = ImageCacheManager.getCachedImage(word.parent)

                                        if (image != null) AsyncImage(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f),
                                            model = image,
                                            contentDescription = null
                                        )
                                    }

                                    Representation.DESCRIPTION.name -> {
                                        word.description?.let {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = it,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.inverseSurface
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                modifier = Modifier
                                    .background(
                                        if (word.representable && word.representation == Representation.ILLUSTRATION.name)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else if (word.representable && word.representation == Representation.DESCRIPTION.name)
                                            MaterialTheme.colorScheme.secondaryContainer
                                        else
                                            MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                    .testTag("text-cue-plain"),
                                text = word.word,
                                fontSize = getFontSize(text, langtag),
                                color = MaterialTheme.colorScheme.inverseSurface
                            )
                            Row(
                                modifier = Modifier.height(20.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                @Suppress("SENSELESS_COMPARISON")
                                if (word.type != null) {
                                    Text(
                                        text = word.type.capitalize(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (word.time != null) {
                                    Spacer(Modifier.size(8.dp))
                                    Text(
                                        text = word.time!!.capitalize(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                        currentIndex += word.word.length
                        foundWord = true
                        break
                    }
                }

                if (!foundWord) {
                    Text(
                        modifier = Modifier
                            .testTag("text-cue-word")
                            .padding(bottom = 20.dp),
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