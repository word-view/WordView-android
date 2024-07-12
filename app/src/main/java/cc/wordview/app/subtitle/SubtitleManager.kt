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

package cc.wordview.app.subtitle

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.text.SubtitleParser
import androidx.media3.extractor.text.webvtt.WebvttParser
import kotlin.time.Duration.Companion.milliseconds

class SubtitleManager {
    private val TAG = "SubtitleManager"
    val cues = ArrayList<WordViewCue>()

    @OptIn(UnstableApi::class)
    fun parseWebvttCues(cuesString: String) {
        WebvttParser().parse(
            cuesString.encodeToByteArray(),
            SubtitleParser.OutputOptions.allCues()
        ) { result ->
            val cue = WordViewCue()

            cue.text = result.cues.first().text.toString()
            cue.startTimeMs = normalize(result.startTimeUs.milliseconds.inWholeMilliseconds.toString()).toInt()
            cue.endTimeMs = normalize(result.endTimeUs.milliseconds.inWholeMilliseconds.toString()).toInt()
            cue.durationMs = normalize(result.durationUs.milliseconds.inWholeMilliseconds.toString()).toInt()

            cues.add(cue)
        }
        Log.i(TAG, "Parsed ${cues.size} cues")
    }

   fun getCueAt(position: Int): WordViewCue {
        for (cue in cues) {
            if (position >= cue.startTimeMs && position <= cue.endTimeMs)
                    return cue
        }

       val voidCue = WordViewCue()
       voidCue.startTimeMs = -1
       return voidCue
   }

    private fun normalize(s: String): String {
        return (if ((s == null || s.length === 0)
        ) null
        else (s.substring(0, s.length - 3)))!!
    }
}
