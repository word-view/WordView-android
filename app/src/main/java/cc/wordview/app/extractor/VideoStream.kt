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

package cc.wordview.app.extractor

import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.StreamingService
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamType

class VideoStream : VideoStreamInterface {
    override var info: StreamInfo =
        StreamInfo(0, "", "", StreamType.VIDEO_STREAM, "", "", 0)

    override var cleanArtistName = ""
    override var cleanTrackName = ""

    override fun init(id: String) {
        info = StreamInfo.getInfo(YTService, "https://youtube.com/watch?v=$id")

        // For now doing this is ok but as the filter grows
        // a less repetitive solution should be created
        val artistClean = info.uploaderName.lowercase()
            .replace("official", "")
            .replace("channel", "")
            .replace("-.*$".toRegex(), "")
            .trim()

        val titleClean = info.name.lowercase()
            .replace("\\[[^\\[]*\\]", "")
            .replace("-.*$".toRegex(), "")
            .replace("[", "")
            .replace("]", "")
            .replace("mv", "")
            .replace("music video", "")
            .replace(
                "歌ってみた",
                ""
            )

        cleanArtistName = artistClean
        cleanTrackName = titleClean
    }

    override fun getStreamURL(): String {
        return info.audioStreams[0].content
    }

    override fun getHQThumbnail(): String {
        return try {
            info.thumbnails.last().url
        } catch (e: Throwable) {
            ""
        }
    }

    companion object {
        var YTService: StreamingService = NewPipe.getService(0);
    }
}