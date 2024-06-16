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

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

object AudioPlayer : MediaPlayer() {
    private const val TAG = "AudioPlayer"
    private var initialized = false;

    fun initialize(dataSource: String) {
        Log.d(TAG, "Initializing MediaPlayer with dataSource: $dataSource")

        if (initialized) reset()
        try {
            this.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(dataSource)
                initialized = true
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    /**
     * If the media is playing then this will pause it, if not it will play the media.
     */
    fun togglePlay(onPlay: () -> Unit, onPause: () -> Unit) {
        if (this.isPlaying) {
            pause()
            onPause()
        } else {
            start()
            onPlay()
        }
    }

    fun skipForward() {
        val position = currentPosition + 5000

        if (position > duration) {
            seekTo(duration)
        } else seekTo(position)
    }

    fun skipBackward() {
        val position = currentPosition - 5000

        if (position < 0) {
            seekTo(0)
        } else seekTo(position)
    }
}