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

package cc.wordview.app.ui.components

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.wordview.app.api.entity.User
import cc.wordview.app.api.getStoredJwt
import cc.wordview.app.ui.activities.auth.AuthActivity
import cc.wordview.app.ui.activities.home.viewmodel.ProfilePictureViewModel

@Preview
@Composable
fun ProfilePicture(viewModel: ProfilePictureViewModel = viewModel(), onClick: (User) -> Unit = {}) {
    val jwt = getStoredJwt()

    val context = LocalContext.current
    val activity = LocalActivity.current

    val user by viewModel.user.collectAsStateWithLifecycle()

    fun openLoginScreen() {
        val intent = Intent(context, AuthActivity::class.java)
        context.startActivity(intent)
        activity?.finish()
    }

    OneTimeEffect { viewModel.makeMeRequest(jwt, context) }

    val logged = user.id != "-1"

    Space(10.0.dp)
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (logged) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimaryContainer)
            .clickable(
                onClick = {
                    if (logged) onClick.invoke(user)
                    else openLoginScreen()
                },
                enabled = true,
            ),
        contentAlignment = Alignment.Center
    ) {
        if (logged) {
            Text(
                text = user.username[0].toString().uppercase(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Person,
                modifier = Modifier.size(24.dp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
    Space(10.0.dp)
}