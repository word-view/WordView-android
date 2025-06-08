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

package cc.wordview.app.ui.screens.search

import cc.wordview.app.ui.activities.home.composables.search.SearchRepository
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.Image.ResolutionLevel
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamType
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class MockSearchRepositoryImpl @Inject constructor() : SearchRepository {
    private val samples: List<StreamInfoItem> = listOf(
        songConstructor("https://www.youtube.com/watch?v=LfephiFN76E", "No Title", "REOL"),
        songConstructor("https://www.youtube.com/watch?v=hxSg2Ioz3LM", "Hibana", "DECO*27"),
        songConstructor("https://www.youtube.com/watch?v=jg09lNupc1s", "Readymade", "Ado"),
    )

    override fun search(query: String): List<StreamInfoItem> {
        return when (query) {
            "single" -> listOf(samples.first())
            "multi" -> samples
            "nonet" -> throw TimeoutException("Mock connection error")
            else -> emptyList()
        }
    }
    override fun searchNextPage(query: String): List<StreamInfoItem> {
        return emptyList()
    }

    private fun songConstructor(url: String, name: String, uploaderName: String): StreamInfoItem {
        val song = StreamInfoItem(
            0,
            url,
            name,
            StreamType.VIDEO_STREAM
        )

        song.uploaderName = uploaderName
        song.isUploaderVerified = true
        song.duration = 300L
        song.thumbnails = listOf(
            Image(
                "",
                1,
                1,
                ResolutionLevel.LOW
            )
        )

        return song
    }
}