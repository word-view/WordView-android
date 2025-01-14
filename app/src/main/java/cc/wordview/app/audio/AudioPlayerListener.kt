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

package cc.wordview.app.audio

import androidx.media3.common.Player
import timber.log.Timber

class AudioPlayerListener : Player.Listener {
    var onTogglePlay: (Boolean) -> Unit = {}
    var onPlaybackEnd = {}
    var onBuffering = {}
    var onReady = {}

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Timber.v("isPlaying=$isPlaying")

        onTogglePlay(isPlaying)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        Timber.v("playbackState=$playbackState")

        when (playbackState) {
            Player.STATE_BUFFERING -> onBuffering()
            Player.STATE_ENDED -> onPlaybackEnd()
            Player.STATE_IDLE -> {}
            Player.STATE_READY -> onReady()
        }
    }
}