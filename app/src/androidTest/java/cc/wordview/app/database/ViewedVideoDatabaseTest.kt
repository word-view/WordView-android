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

package cc.wordview.app.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cc.wordview.app.database.entity.ViewedVideo
import cc.wordview.app.database.entity.ViewedVideoDAO
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ViewedVideoDatabaseTest {
    private lateinit var viewedVideoDAO: ViewedVideoDAO
    private lateinit var database: WordViewDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        RoomAccess.open(context, inMemory = true)

        database = RoomAccess.getDatabase()
        viewedVideoDAO = database.viewedVideoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun savesViewedVideo() {
        val video = ViewedVideo(
            id = "0a10a10a",
            title = "Song title",
            artist = "Song artist",
            thumbnailUrl = "https://api.wordview.cc/a.png",
            duration = 120
        )
        viewedVideoDAO.insertAll(video)

        val savedVideo = viewedVideoDAO.getAll().singleOrNull()

        assert(savedVideo?.id == video.id)
        assert(savedVideo?.title == video.title)
        assert(savedVideo?.artist == video.artist)
        assert(savedVideo?.thumbnailUrl == video.thumbnailUrl)
        assert(savedVideo?.duration == video.duration)
    }
}