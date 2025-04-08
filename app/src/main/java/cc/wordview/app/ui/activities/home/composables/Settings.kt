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

package cc.wordview.app.ui.activities.home.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Carpenter
import androidx.compose.material.icons.outlined.NetworkPing
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.navigation.NavHostController
import cc.wordview.app.BuildConfig
import cc.wordview.app.R
import cc.wordview.app.extensions.goBack
import cc.wordview.app.extensions.languageDisplayName
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.ui.theme.poppinsFamily
import cc.wordview.gengolex.Language
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.switchPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavHostController) {
    val langTag = AppSettings.language.get()
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = LocalContentColor.current
            ),
            title = {
                Text(
                    stringResource(R.string.settings),
                    fontFamily = poppinsFamily,
                )
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
                    enabled = { false },
                    defaultValue = BuildConfig.API_BASE_URL,
                    values = listOf(),
                    title = { Text(
                        text = stringResource(R.string.api_endpoint),
                        fontFamily = poppinsFamily
                    ) },
                    summary = { Text(text = it) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.NetworkPing,
                            contentDescription = null
                        )
                    },
                )
                listPreference(
                    key = AppSettings.language.key,
                    defaultValue = AppSettings.language.defaultValue,
                    values = listOf(
                        "pt",
                        "ja",
                        "en"
                    ),

                    valueToText = { value ->
                        buildAnnotatedString {
                            append(value.languageDisplayName())
                        }
                    },

                    title = {
                        Text(
                            text = stringResource(R.string.learning_language),
                            fontFamily = poppinsFamily,
                        )
                    },
                    summary = {
                        Text(
                            text = stringResource(
                                R.string.the_language_that_you_want_to_learn,
                                it.languageDisplayName()
                            )
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Translate,
                            contentDescription = null
                        )
                    },
                    type = ListPreferenceType.ALERT_DIALOG
                )
                @Suppress("KotlinConstantConditions")
                if (BuildConfig.BUILD_TYPE == "debug") {
                    switchPreference(
                        key = AppSettings.composerMode.key,
                        defaultValue = AppSettings.composerMode.defaultValue,
                        title = {
                            Text(
                                text = stringResource(R.string.composer_mode),
                                fontFamily = poppinsFamily
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Carpenter,
                                contentDescription = null
                            )
                        },
                        summary = { Text(text = stringResource(R.string.provides_more_information_in_the_player_that_helps_writing_lyrics)) }
                    )
                }
            }
        }
    }
}