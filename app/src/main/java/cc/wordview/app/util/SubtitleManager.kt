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

package cc.wordview.app.util

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.text.Cue
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.text.SubtitleParser
import androidx.media3.extractor.text.webvtt.WebvttParser

class SubtitleManager {
    private val TAG = "SubtitleManager"
    var cues: ArrayList<Cue> = ArrayList()

    @OptIn(UnstableApi::class)
    fun parseWebvttCues(cuesString: String) {
        WebvttParser().parse(
            cuesString.encodeToByteArray(),
            SubtitleParser.OutputOptions.allCues()
        ) { result ->
            for (cue in result.cues) cues.add(cue)
        }
        Log.i(TAG, "Parsed ${cues.size} cues")
    }
}