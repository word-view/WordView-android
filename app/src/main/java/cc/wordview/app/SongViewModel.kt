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

package cc.wordview.app

import androidx.lifecycle.ViewModel
import cc.wordview.app.extractor.VideoStream
import cc.wordview.app.extractor.VideoStreamInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object SongViewModel : ViewModel() {
    private val _videoId = MutableStateFlow("")
    private val _videoStream = MutableStateFlow<VideoStreamInterface>(VideoStream())

    val videoStream = _videoStream.asStateFlow()
    val videoId = _videoId.asStateFlow()

    fun setVideo(id: String) {
        _videoId.update { id }
    }

    fun setVideoStream(videoStream: VideoStreamInterface) {
        _videoStream.update { videoStream }
    }
}