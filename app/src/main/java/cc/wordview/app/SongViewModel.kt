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

import android.util.Log
import androidx.lifecycle.ViewModel
import cc.wordview.app.api.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object SongViewModel : ViewModel() {
    private val TAG = SongViewModel::class.java.simpleName
    private val _video = MutableStateFlow(Video())

    val video: StateFlow<Video> = _video.asStateFlow()

    fun setVideo(vid: Video) {
        // For now doing this is ok but as the filter grows
        // a less repetitive solution should be created
        val artistClean = vid.artist.lowercase()
            .replace("official", "")
            .replace("channel", "")

        val titleClean = vid.title.lowercase()
            .replace("\\[[^\\[]*\\]", "")
            .replace("[", "")
            .replace("]", "")
            .replace("MV", "")
            .replace("Music Video", "")
            .replace(
                "歌ってみた",
                ""
            )

        vid.searchQuery = "$titleClean $artistClean"

        _video.update { oldValue ->
            Log.d(TAG, "Updating working video from '${oldValue.title}' to '${vid.title}'")
            vid
        }
    }
}