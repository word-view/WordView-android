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

package cc.wordview.app.ui.activities.auth

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import cc.wordview.app.ui.activities.WordViewActivity
import cc.wordview.app.ui.activities.auth.composables.LoginScreen
import cc.wordview.app.ui.activities.auth.composables.RegisterScreen
import cc.wordview.app.ui.theme.WordViewTheme
import com.composegears.tiamat.compose.Navigation
import com.composegears.tiamat.compose.navigationNone
import com.composegears.tiamat.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import me.zhanghai.compose.preference.ProvidePreferenceLocals

@AndroidEntryPoint
class AuthActivity : WordViewActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            BackHandler {}

            val authNavController = rememberNavController(
                key = "authNavController",
                startDestination = LoginScreen,
                configuration = {},
            )

            WordViewTheme {
                ProvidePreferenceLocals {
                    Navigation(
                        navController = authNavController,
                        modifier = Modifier.fillMaxSize(),
                        destinations = arrayOf(LoginScreen, RegisterScreen),
                        contentTransformProvider = { navigationNone() }
                    )
                }
            }
        }
    }
}