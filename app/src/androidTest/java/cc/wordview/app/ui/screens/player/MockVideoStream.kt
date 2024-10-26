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

package cc.wordview.app.ui.screens.player

import cc.wordview.app.extractor.VideoStreamInterface
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamType

class MockVideoStream : VideoStreamInterface {
    override var info: StreamInfo =
        StreamInfo(0, "", "", StreamType.VIDEO_STREAM, "", "Gran Vals - Francisco Tárrega", 0)

    override var searchQuery: String = ""
    var url = "http://10.0.2.2:8080/public/gran_vals_tarrega.mp3"

    override fun init(id: String) {}

    override fun getStreamURL(): String {
        return url
    }

    override fun getHQThumbnail(): String {
        return ""
    }
}