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

package cc.wordview.app.subtitle

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.text.SubtitleParser
import androidx.media3.extractor.text.webvtt.WebvttParser
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
class Lyrics(vttLyrics: String) : ArrayList<WordViewCue>() {
    init {
        if (vttLyrics.isNotEmpty()) {
            WebvttParser().parse(
                vttLyrics.encodeToByteArray(), SubtitleParser.OutputOptions.allCues()
            ) { result ->
                if (result.cues.first().text?.isNotEmpty()!!) {
                    val text = result.cues.first().text.toString().trim()

                    this.add(
                        WordViewCue(
                            text,
                            normalize(result.startTimeUs.milliseconds.inWholeMilliseconds),
                            normalize(result.endTimeUs.milliseconds.inWholeMilliseconds),
                        )
                    )
                }
            }
            Timber.d("Parsed ${this.size} cues")
        }
    }

    fun getCueAt(position: Int): WordViewCue {
        for (cue in this) {
            if (position >= cue.startTimeMs && position <= cue.endTimeMs) return cue
        }

        // a empty cue used to ignore if no cue was found.
        return WordViewCue("", -1, -1)
    }

    private fun normalize(num: Long): Int {
        if (num == 0L) return 0

        val s = num.toString()
        return (if ((s == null || s.length === 0)) null
        else (s.substring(0, s.length - 3)))!!.toInt()
    }
}