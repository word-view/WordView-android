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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cc.wordview.app.R
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SongCard(
    modifier: Modifier = Modifier,
    thumbnail: String,
    artist: String,
    trackName: String,
    language: Language? = null,
    onClick: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()

    fun onClickCard() = coroutineScope.launch {
        // delay so that the animation can be seen
        delay(120)
        onClick()
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        onClick = { onClickCard() }
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(120.dp)) {
                if (language != null) Image(
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.TopEnd)
                        .testTag("$language icon"),
                    painter = getIconForLang(language),
                    contentDescription = null
                )
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(-1f),
                    shape = DefaultRoundedCornerShape
                ) {
                    Box(
                        Modifier
                            .zIndex(-1f)
                            .alpha(0.1f)
                            .background(MaterialTheme.colorScheme.onBackground)
                    )
                    AsyncImage(
                        model = thumbnail,
                        placeholder = null,
                        error = painterResource(id = if (isSystemInDarkTheme()) R.drawable.nonet else R.drawable.nonet_dark),
                        contentDescription = "$trackName Cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillHeight,
                    )
                }
            }
            Column(
                Modifier
                    .width(120.dp)
                    .padding(top = 5.dp)
            ) {
                Text(
                    text = trackName,
                    style = Typography.labelMedium,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = artist,
                    style = Typography.labelSmall,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    }
}

@Composable
fun getIconForLang(language: Language): Painter {
    return when (language) {
        Language.ENGLISH -> painterResource(id = R.drawable.us)
        Language.JAPANESE -> painterResource(id = R.drawable.ja)
        Language.PORTUGUESE -> painterResource(id = R.drawable.br)
    }
}