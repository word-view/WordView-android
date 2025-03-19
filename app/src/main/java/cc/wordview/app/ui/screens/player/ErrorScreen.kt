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

package cc.wordview.app.ui.screens.player

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.R
import cc.wordview.app.SongViewModel
import cc.wordview.app.extensions.goBack
import cc.wordview.app.extensions.setOrientationUnspecified
import cc.wordview.app.ui.theme.Typography

@Composable
fun ErrorScreen(navHostController: NavHostController, message: String, refresh: () -> Unit, statusCode: Int) {
    val videoStream by SongViewModel.videoStream.collectAsStateWithLifecycle()
    val activity = LocalActivity.current!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("error-screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(180.dp),
            painter = if (statusCode == 404) painterResource(id = R.drawable.nolyrics) else painterResource(id = R.drawable.radio),
            contentDescription = null
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.an_error_has_occurred),
            textAlign = TextAlign.Center,
            style = Typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = if (statusCode == 404) stringResource(
                R.string.couldn_t_find_any_lyrics_for,
                videoStream.info.name
            ) else message,
            textAlign = TextAlign.Center,
            style = Typography.bodySmall,
            fontWeight = FontWeight.Light,
        )
        Spacer(Modifier.size(15.dp))
        Button(
            modifier = Modifier.testTag("error-back-button"),
            onClick = { refresh() }) {
            Text(text = stringResource(R.string.try_again))
        }
        Button(
            modifier = Modifier.testTag("error-back-button"),
            onClick = {
                activity.setOrientationUnspecified()
                navHostController.goBack()
            }) {
            Text(text = stringResource(R.string.go_back))
        }
    }
}