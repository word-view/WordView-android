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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cc.wordview.app.api.APICallback
import cc.wordview.app.api.search
import cc.wordview.app.extensions.goBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(navController: NavHostController) {
    val context = LocalContext.current

    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var leadingIcon by remember { mutableStateOf(Icons.Filled.Search) }
    var waitingForResponse by remember { mutableStateOf(false) }

    val callback = object : APICallback {
        override fun onSuccessResponse(response: String?) {
            if (response != null) {
                Log.i("Search", "$response")
                waitingForResponse = false
            }
        }

        override fun onErrorResponse(response: String?) {
            TODO("Not yet implemented")
        }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (waitingForResponse) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}