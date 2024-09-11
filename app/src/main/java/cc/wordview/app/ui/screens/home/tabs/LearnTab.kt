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

package cc.wordview.app.ui.screens.home.tabs

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cc.wordview.app.R
import cc.wordview.app.SongViewModel
import cc.wordview.app.api.Video
import cc.wordview.app.ui.screens.util.Screen
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.ui.theme.Typography
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@SuppressLint("MutableCollectionMutableState")
@Composable
fun LearnTab(navController: NavHostController, navHostController: NavHostController) {
    var videos by remember { mutableStateOf(ArrayList<Video>()) }

    LaunchedEffect(Unit) {
        val typeToken = object : TypeToken<ArrayList<Video>>() {}.type

        val parsed = Gson().fromJson<ArrayList<Video>>(
            "[{\"id\":\"D0ehC_8sQuU\",\"title\":\"It's raining after all\",\"artist\":\"TUYU\",\"cover\":\"https://img.youtube.com/vi/D0ehC_8sQuU/0.jpg\"}, {\"id\":\"vcw5THyM7Jo\",\"title\":\"If there was an endpoint\",\"artist\":\"TUYU\",\"cover\":\"https://img.youtube.com/vi/vcw5THyM7Jo/0.jpg\"}, {\"id\":\"gqiRJn7me-s\",\"title\":\"君に最後の口づけを\",\"artist\":\"majiko\",\"cover\":\"https://img.youtube.com/vi/gqiRJn7me-s/0.jpg\"}]",
            typeToken
        )

        videos = parsed
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(PaddingValues(start = 6.dp))
    ) {
        LazyRow(modifier = Modifier.fillMaxWidth(), state = rememberLazyListState()) {
            for (video in videos) {
                item {
                    Card(
                        modifier = Modifier.testTag("song-card"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                        ),
                        onClick = {
                            SongViewModel.setVideo(video)
                            navHostController.navigate(Screen.Player.route)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (video.id != "") {
                                Surface(
                                    modifier = Modifier.size(120.dp),
                                    shape = DefaultRoundedCornerShape
                                ) {
                                    AsyncImage(
                                        model = video.cover,
                                        placeholder = painterResource(id = R.drawable.radio),
                                        error = painterResource(id = R.drawable.radio),
                                        contentDescription = "${video.title} cover",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.FillHeight,
                                    )
                                }
                                Column(
                                    Modifier
                                        .width(120.dp)
                                        .padding(top = 5.dp)
                                ) {
                                    Text(
                                        text = video.title,
                                        style = Typography.labelMedium,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    Text(
                                        text = video.artist,
                                        style = Typography.labelSmall,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.inverseSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
