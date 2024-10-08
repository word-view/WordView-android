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

package cc.wordview.app.ui.screens.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cc.wordview.app.R
import cc.wordview.app.extensions.goBack
import cc.wordview.app.ui.theme.Typography

@Composable
fun ErrorScreen(cleanup: () -> Unit, navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("error-screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(180.dp),
            painter = painterResource(id = R.drawable.radio),
            contentDescription = null
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "An error has occurred \nand the audio could not be played.",
            textAlign = TextAlign.Center,
            style = Typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.size(15.dp))
        Button(
            modifier = Modifier.testTag("error-back-button"),
            onClick = { cleanup(); navHostController.goBack() }) {
            Text(text = "Go back to the home screen")
        }
    }
}