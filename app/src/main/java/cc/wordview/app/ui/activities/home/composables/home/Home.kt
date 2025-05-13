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

package cc.wordview.app.ui.activities.home.composables.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import cc.wordview.app.api.getStoredJwt
import cc.wordview.app.ui.activities.home.HomeNav
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.theme.redhatFamily
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navHostController: NavHostController) {
    val jwt = getStoredJwt()

    OneTimeEffect {
        Timber.i("Hello $jwt!")
    }

    BackHandler {}
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(
                    text = "WordView",
                    fontWeight = FontWeight.Bold,
                    fontFamily = redhatFamily
                )
            },
            actions = {
                IconButton(
                    modifier = Modifier.testTag("search"),
                    onClick = { navHostController.navigate(HomeNav.Search.route) }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(
                    modifier = Modifier.testTag("settings"),
                    onClick = { navHostController.navigate(HomeNav.Settings.route) }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        )
    }) { innerPadding ->
        LearnTab(innerPadding)
    }
}