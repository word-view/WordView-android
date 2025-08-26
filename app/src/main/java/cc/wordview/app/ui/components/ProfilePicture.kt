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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.GlobalViewModel
import cc.wordview.app.R
import cc.wordview.app.api.getStoredJwt
import cc.wordview.app.api.setStoredJwt
import cc.wordview.app.components.OneTimeEffect
import cc.wordview.app.extensions.openActivity
import cc.wordview.app.ui.activities.auth.AuthActivity
import cc.wordview.app.ui.activities.player.PlayerActivity
import kotlin.math.log

@Preview
@Composable
fun ProfilePicture(modifier: Modifier = Modifier, onNavigateToProfile: () -> Unit = {}, onOpenHistory: () -> Unit = {}) {
    val jwt = getStoredJwt()

    val context = LocalContext.current
    val activity = LocalActivity.current

    val user by GlobalViewModel.user.collectAsStateWithLifecycle()

    var showMenu by remember { mutableStateOf(false) }

    fun openLoginScreen() {
        context.openActivity<AuthActivity>()
        activity?.finish()
    }

    fun logout() {
        setStoredJwt(null, context)
        GlobalViewModel.resetUser()
        context.openActivity<AuthActivity>()
        activity?.finish()
    }

    OneTimeEffect { GlobalViewModel.makeMeRequest(jwt, context) }

    val logged = user != null

    Space(10.0.dp)
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (logged) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimaryContainer)
            .clickable(
                onClick = { showMenu = !showMenu },
                enabled = true,
            ),
        contentAlignment = Alignment.Center
    ) {
        if (logged) {
            Text(
                text = user!!.username[0].toString().uppercase(),
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
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false },
        offset = DpOffset(x = 64.dp, y = 0.dp)
    ) {
        if (logged) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.profile)) },
                onClick = {
                    showMenu = false
                    onNavigateToProfile()
                }
            )
        } else {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.log_in)) },
                onClick = {
                    showMenu = false
                    openLoginScreen()
                }
            )
        }
        DropdownMenuItem(
            text = { Text(stringResource(R.string.history)) },
            onClick = {
                showMenu = false
                onOpenHistory()
            }
        )

        if (logged) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.log_out)) },
                onClick = {
                    showMenu = false
                    logout()
                }
            )
        }
    }
    Space(10.0.dp)
}