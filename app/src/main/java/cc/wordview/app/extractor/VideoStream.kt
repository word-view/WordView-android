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

package cc.wordview.app.extractor

import android.content.Context
import android.graphics.Bitmap
import cc.wordview.app.misc.ImageCacheManager
import coil3.request.ImageRequest
import coil3.request.allowHardware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.StreamingService
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamType

class VideoStream : VideoStreamInterface {
    override var info: StreamInfo =
        StreamInfo(0, "", "", StreamType.VIDEO_STREAM, "", "", 0)

    override var cleanArtistName = ""
    override var cleanTrackName = ""

    override fun init(id: String, context: Context) {
        info = StreamInfo.getInfo(YTService, "https://youtube.com/watch?v=$id")

        CoroutineScope(Dispatchers.IO).launch {
            val request = ImageRequest.Builder(context)
                .data(info.thumbnails.last().url)
                .allowHardware(true)
                .diskCacheKey("$id-background")
                // Memory cache key is only used by the ImageCacheManager to keep track of it
                .memoryCacheKey("$id-background")

        if (getHQThumbnail() == null) {
            ImageCacheManager.enqueue(request)
        }

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

    override fun getHQThumbnail(): Bitmap? {
        return ImageCacheManager.getDiskCachedImage("${info.id}-background")
    }

    companion object {
        var YTService: StreamingService = NewPipe.getService(0)
    }
}