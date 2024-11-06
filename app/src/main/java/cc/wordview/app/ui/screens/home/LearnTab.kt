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

package cc.wordview.app.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cc.wordview.app.SongViewModel
import cc.wordview.app.audio.Video
import cc.wordview.app.ui.components.SongCard
import cc.wordview.app.ui.screens.components.Screen
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import com.gigamole.composefadingedges.horizontalFadingEdges

@SuppressLint("MutableCollectionMutableState")
@Composable
fun LearnTab(navController: NavHostController, navHostController: NavHostController) {
    var editorsPick by remember { mutableStateOf(ArrayList<Video>()) }

    LaunchedEffect(Unit) {
        editorsPick = arrayListOf(
            Video(
                "ZnUEeXpxBJ0",
                "Aquarela",
                "Toquinho",
                "https://i.ytimg.com/vi_webp/ZnUEeXpxBJ0/maxresdefault.webp",
                Language.PORTUGUESE
            ),
            Video(
                "ZpT9VCUS54s",
                "Suisei no parade",
                "majiko",
                "https://i.ytimg.com/vi_webp/ZpT9VCUS54s/maxresdefault.webp",
                Language.JAPANESE
            ),
            Video(
                "HCTunqv1Xt4",
                "When I'm Sixty Four",
                "The Beatles",
                "https://i.ytimg.com/vi_webp/HCTunqv1Xt4/maxresdefault.webp",
                Language.ENGLISH
            )
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(PaddingValues(top = 17.dp))
    ) {
        Text(
            text = "Editor's pick",
            textAlign = TextAlign.Center,
            style = Typography.titleLarge,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 17.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalFadingEdges(),
            state = rememberLazyListState(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.size(6.dp)) }

            var i = 0
            items(editorsPick, key = { it.id }) {
                i += 1
                SongCard(
                    modifier = Modifier
                        .testTag("song-card")
                        .animateItem(fadeInSpec = tween(durationMillis = i * 500)),
                    thumbnail = it.cover,
                    artist = it.artist,
                    trackName = it.title,
                    language = it.language
                ) {
                    SongViewModel.setVideo(it.id)
                    navHostController.navigate(Screen.Player.route)
                }
            }

            item { Spacer(Modifier.size(128.dp)) }
        }
    }
}
