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

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cc.wordview.app.R
import cc.wordview.app.api.APICallback
import cc.wordview.app.api.Video
import cc.wordview.app.api.getHistory
import cc.wordview.app.currentSong
import cc.wordview.app.ui.screens.Screen
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.ui.theme.Typography
import coil.compose.AsyncImage
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnTab(navController: NavHostController, navHostController: NavHostController) {
    val context = LocalContext.current
    var json by remember { mutableStateOf(Video()) }

    val callback = object : APICallback {
        override fun onSuccessResponse(response: String?) {
            if (response != null) {
                Log.i("LearnTab", response)
                json = Gson().fromJson(response, Video::class.java)
            }
        }

        override fun onErrorResponse(response: String?) {
            TODO("Not yet implemented")
        }
    }

    LaunchedEffect(Unit) { getHistory(callback, context) }

    Box(
        Modifier
            .fillMaxSize()
            .padding(PaddingValues(start = 6.dp))
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            onClick = { currentSong = json; navHostController.navigate(Screen.Player.route) }
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (json.id != "") {
                    Surface(modifier = Modifier.size(120.dp), shape = DefaultRoundedCornerShape) {
                        AsyncImage(
                            model = json.cover,
                            placeholder = painterResource(id = R.drawable.radio),
                            error = painterResource(id = R.drawable.radio),
                            contentDescription = "${json.title} cover",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Column(
                        Modifier
                            .width(120.dp)
                            .padding(top = 5.dp)) {
                        Text(
                            text = json.title,
                            style = Typography.labelMedium,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            text = json.artist,
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
