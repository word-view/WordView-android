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
import androidx.compose.material.icons.outlined.Carpenter
import androidx.compose.material.icons.outlined.NetworkPing
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import cc.wordview.app.BuildConfig
import cc.wordview.app.R
import cc.wordview.app.components.ui.BackTopAppBar
import cc.wordview.app.extensions.localizedDisplayName
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.ui.theme.poppinsFamily
import cc.wordview.gengolex.Language
import com.composegears.tiamat.compose.back
import com.composegears.tiamat.compose.navController
import com.composegears.tiamat.compose.navDestination
import com.composegears.tiamat.navigation.NavDestination
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.switchPreference

val SettingsScreen: NavDestination<Unit> by navDestination {
    val navController = navController()

    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        BackTopAppBar(
            title = {
                Text(
                    stringResource(R.string.settings),
                    fontFamily = poppinsFamily,
                )
            },
            onClickBack = { navController.back() }
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
                            append(Language.byTag(value).localizedDisplayName(context))
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
                                Language.byTag(it).localizedDisplayName()
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
                @Suppress("KotlinConstantConditions", "SimplifyBooleanWithConstants")
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