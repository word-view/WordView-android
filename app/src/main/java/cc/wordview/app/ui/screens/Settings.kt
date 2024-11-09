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

package cc.wordview.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Carpenter
import androidx.compose.material.icons.outlined.NetworkPing
import androidx.compose.material.icons.outlined.ShortText
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.extensions.getOrDefault
import cc.wordview.app.extensions.goBack
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.LocalPreferenceFlow
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.switchPreference

val defaultSettings = hashMapOf(
    "api_endpoint" to "http://10.0.2.2:8080",
    "language" to "ja",
    "composer_mode" to false,
    "filter_romanizations" to true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavHostController) {
    val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = LocalContentColor.current
            ),
            title = {
                Text("Settings")
            },
            navigationIcon = {
                IconButton(onClick = { navController.goBack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }
        )
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                listPreference(
                    key = "api_endpoint",
                    defaultValue = "http://10.0.2.2:8080",
                    values = listOf(
                        "http://10.0.2.2:8080",
                        "http://192.168.1.100:8080",
                        "https://api.wordview.cc"
                    ),
                    title = { Text(text = "API endpoint") },
                    summary = { Text(text = it) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.NetworkPing,
                            contentDescription = null
                        )
                    },
                    type = ListPreferenceType.ALERT_DIALOG
                )
                listPreference(
                    key = "language",
                    defaultValue = "ja",
                    values = listOf(
                        "pt",
                        "ja",
                        "en"
                    ),
                    title = { Text(text = "Learning language") },
                    summary = { Text(text = "The language that you want to learn: $it") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Translate,
                            contentDescription = null
                        )
                    },
                    type = ListPreferenceType.ALERT_DIALOG
                )
                switchPreference(
                    key = "composer_mode",
                    defaultValue = false,
                    title = { Text(text = "Composer Mode") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Carpenter,
                            contentDescription = null
                        )
                    },
                    summary = { Text(text = "Provides more information in the player that helps writing lyrics") }
                )
                switchPreference(
                    key = "filter_romanizations",
                    enabled = { preferences.getOrDefault<String>("language") == "ja" },
                    defaultValue = true,
                    title = { Text(text = "Filter romanizations") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.ShortText,
                            contentDescription = null
                        )
                    },
                    summary = { Text(text = "Attempts to remove romanizations from lyrics of non alphabetic languages") }
                )
            }
        }
    }
}