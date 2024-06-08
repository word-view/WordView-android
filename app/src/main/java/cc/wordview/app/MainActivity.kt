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

package cc.wordview.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.api.Video
import cc.wordview.app.ui.screens.LanguagePicker
import cc.wordview.app.ui.screens.Login
import cc.wordview.app.ui.screens.Player
import cc.wordview.app.ui.screens.Settings
import cc.wordview.app.ui.screens.Welcome
import cc.wordview.app.ui.screens.home.Home
import cc.wordview.app.ui.theme.WordViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordViewTheme {
                AppNavigationHost()
            }
        }
    }
}

// TODO: quick hack, find a better solution for sending the picked song at the learn tab to the Music Screen
var currentSong by mutableStateOf(Video())

@Composable
fun AppNavigationHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("welcome") {
            Welcome(navController)
        }

        composable("login") {
            Login(navController)
        }

        composable("language-picker") {
            LanguagePicker(navController)
        }

        composable("settings") {
            Settings(navController)
        }

        composable("player") {
            Player(navController)
        }

        composable("home") {
            Home(navController)
        }
    }
}