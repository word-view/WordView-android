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

package cc.wordview.app.ui.activities.player

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import cc.wordview.app.extractor.DownloaderImpl
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.activities.player.composables.Player
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.theme.WordViewTheme
import dagger.hilt.android.AndroidEntryPoint
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.schabi.newpipe.extractor.NewPipe
import timber.log.Timber

@AndroidEntryPoint
class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
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

            WordViewTheme {
                ProvidePreferenceLocals {
                    Player()
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
    }
}