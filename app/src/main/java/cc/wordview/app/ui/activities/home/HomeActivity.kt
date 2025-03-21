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

package cc.wordview.app.ui.activities.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.extractor.DownloaderImpl
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.theme.WordViewTheme
import dagger.hilt.android.AndroidEntryPoint
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.schabi.newpipe.extractor.NewPipe
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            WordViewTheme {
                ProvidePreferenceLocals {
                    val context = LocalContext.current

                    OneTimeEffect {
                        // If no trees were planted, we can assume that
                        // this activity was started separately
                        if (Timber.treeCount == 0) {
                            DownloaderImpl.init(null)
                            NewPipe.init(DownloaderImpl.getInstance())

                            Timber.plant(Timber.DebugTree())
                            ImageCacheManager.init(context)
                        }
                    }

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {
                        for (screen in HomeNav.screens) {
                            composable(screen.route) {
                                screen.Composable(navHostController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}