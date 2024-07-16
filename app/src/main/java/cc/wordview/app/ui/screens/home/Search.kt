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
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import cc.wordview.app.api.VideoSearchResult
import cc.wordview.app.api.search
import cc.wordview.app.currentSong
import cc.wordview.app.extensions.goBack
import cc.wordview.app.ui.screens.util.Screen
import cc.wordview.app.ui.theme.Typography
import coil.compose.AsyncImage
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(navController: NavHostController) {
    val context = LocalContext.current

    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var leadingIcon by remember { mutableStateOf(Icons.Filled.Search) }
    var waitingForResponse by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf(ArrayList<VideoSearchResult>()) }

    val callback = object : APICallback {
        override fun onSuccessResponse(response: String?) {
            if (response != null) {
                Log.i("Search", "$response")


                val typeToken = object : TypeToken<List<VideoSearchResult>>() {}.type
                results = Gson().fromJson<List<VideoSearchResult>>(
                    response,
                    typeToken
                ) as ArrayList<VideoSearchResult>

                waitingForResponse = false
            }
        }

        override fun onErrorResponse(response: VolleyError) {
            waitingForResponse = false
            Log.e("Search", response.stackTraceToString())
            // showing the entire stack trace here is weird, but its probably better than showing null
            Toast.makeText(context, "Request failed: ${response.stackTraceToString()}", Toast.LENGTH_LONG).show()
        }
    }


    fun playResult(result: VideoSearchResult) {
        val video = Video(
            result.id,
            result.title,
            result.channel,
            "https://img.youtube.com/vi/${result.id}/0.jpg"
        )

        currentSong = video
        navController.navigate(Screen.Player.route)
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SearchBar(
                placeholder = {
                    Text("Search for music, artists, albums...")
                },
                query = searchText,
                onQueryChange = { query -> searchText = query },
                onSearch = { query ->
                    search(query, callback, context)
                    waitingForResponse = true
                    isSearching = false

                    // we need to manually fix the icon cause onActiveChange
                    // is not called if we change isSearching.
                    leadingIcon = Icons.Filled.Search
                },
                active = isSearching,
                onActiveChange = { active ->
                    isSearching = active
                    leadingIcon = if (active) Icons.Filled.ArrowBack else Icons.Filled.Search
                },
                leadingIcon = {
                    // This button will act only as a icon in
                    // the case of the searchbar being deselected
                    IconButton(onClick = { navController.goBack() }, enabled = isSearching) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = ""
                        )
                    }
                },
            ) {

            }
        }
    }) { innerPadding ->
        if (waitingForResponse) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (result in results) {
                    Spacer(Modifier.size(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                        ),
                        onClick = { playResult(result) }
                    ) {
                        Row {
                            Surface(
                                modifier = Modifier
                                    .size(72.dp)
                                    .padding(8.dp),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                AsyncImage(
                                    model = "https://img.youtube.com/vi/${result.id}/0.jpg",
                                    placeholder = painterResource(id = R.drawable.radio),
                                    error = painterResource(id = R.drawable.radio),
                                    contentDescription = "${result.title} cover",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                            Column(Modifier.padding(8.dp)) {
                                Text(
                                    text = result.title,
                                    style = Typography.labelMedium,
                                    textAlign = TextAlign.Left,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Text(
                                    text = result.channel,
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