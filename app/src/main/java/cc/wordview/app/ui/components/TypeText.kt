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

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import cc.wordview.app.components.ui.OneTimeEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TypeText(
    text: String,
    textAlign: TextAlign? = null,
    style: TextStyle = LocalTextStyle.current
) {
    var partText by remember { mutableStateOf("") }
    val highlightColor = MaterialTheme.colorScheme.onPrimary

    OneTimeEffect {
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)

            text.forEachIndexed { charIndex, _ ->
                partText = text.substring(startIndex = 0, endIndex = charIndex + 1)
                delay(100)
            }
        }
    }

    Text(
        modifier = Modifier
            .drawBehind {
                val highlightHeight = size.height / 2.5f

                drawRect(
                    color = highlightColor,
                    topLeft = Offset(0f, (size.height - highlightHeight) / 2f),
                    size = Size(size.width, highlightHeight)
                )
            }

            .testTag("type-text"),
        text = partText,
        textAlign = textAlign,
        style = style,
    )
}
