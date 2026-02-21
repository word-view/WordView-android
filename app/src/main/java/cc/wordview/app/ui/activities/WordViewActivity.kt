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

package cc.wordview.app.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import cc.wordview.app.database.RoomAccess
import cc.wordview.app.extractor.DownloaderImpl
import cc.wordview.app.misc.ImageCacheManager
import org.schabi.newpipe.extractor.NewPipe
import timber.log.Timber

abstract class WordViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If no trees were planted, we can assume that
        // this activity was started separately
        if (Timber.treeCount == 0) {
            RoomAccess.open(applicationContext)

            DownloaderImpl.init(null)
            NewPipe.init(DownloaderImpl.getInstance())

            ImageCacheManager.init(baseContext)

            Timber.plant(Timber.DebugTree())
        }
    }
}