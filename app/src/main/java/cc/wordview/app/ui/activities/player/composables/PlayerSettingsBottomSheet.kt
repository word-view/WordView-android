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

package cc.wordview.app.ui.activities.player.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hardware
import androidx.compose.material.icons.outlined.PhotoSizeSelectActual
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cc.wordview.app.BuildConfig
import cc.wordview.app.R
import cc.wordview.app.components.ui.Space
import cc.wordview.app.misc.PlayerSettings
import cc.wordview.app.ui.theme.poppinsFamily
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.switchPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSettingsBottomSheet(onDismissRequest: () -> Unit = {}) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Player Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 6.dp).weight(1f),
                textAlign = TextAlign.Left
            )
        }

        Space(16.dp)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            listPreference(
                key = PlayerSettings.playbackSpeed.key,
                defaultValue = PlayerSettings.playbackSpeed.defaultValue,
                values = listOf(
                    1.5f,
                    1.25f,
                    1.0f,
                    0.75f,
                    0.5f,
                ),
                valueToText = { value ->
                    buildAnnotatedString {
                        append("${value}x")
                    }
                },
                title = {
                    Text(
                        text = "Playback speed",
                        fontFamily = poppinsFamily,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                summary = {
                    Text(
                        text = "${it}x",
                        fontFamily = poppinsFamily,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                type = ListPreferenceType.DROPDOWN_MENU,
            )
            switchPreference(
                key = PlayerSettings.backgroundImage.key,
                defaultValue = PlayerSettings.backgroundImage.defaultValue,
                title = {
                    Text(
                        text = "Background image",
                        fontFamily = poppinsFamily,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                summary = {
                    Text(
                        text = "Toggles the visibility of the background image",
                        fontFamily = poppinsFamily,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.PhotoSizeSelectActual,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
            )
            @Suppress("KotlinConstantConditions", "SimplifyBooleanWithConstants")
            if (BuildConfig.BUILD_TYPE == "debug") {
                switchPreference(
                    key = PlayerSettings.composerMode.key,
                    defaultValue = PlayerSettings.composerMode.defaultValue,
                    title = {
                        Text(
                            text = stringResource(R.string.composer_mode),
                            fontFamily = poppinsFamily,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    summary = {
                        Text(
                            text = stringResource(R.string.provides_more_information_in_the_player_that_helps_writing_lyrics),
                            fontFamily = poppinsFamily,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Hardware,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                )
            }
        }
    }
}