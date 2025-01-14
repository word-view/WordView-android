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

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AudioPlayer {
    private lateinit var player: ExoPlayer

    private var job: Job? = null

    var onPositionChange: (Int, Int) -> Unit = { position: Int, bufferedPercentage: Int -> }
    var onPrepared: () -> Unit = {}
    var onInitializeFail: (Exception) -> Unit = {}

    private val internalListener = object : Player.Listener {
        @SuppressLint("SwitchIntDef")
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> stopPositionCheck()
                Player.STATE_READY -> startPositionCheck()
            }
        }
    }

    fun initialize(url: String, context: Context, listener: AudioPlayerListener) {
        Timber.i("Streaming from $url")

        try {
            player = ExoPlayer.Builder(context).setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(), true
            ).build()

            player.addListener(internalListener)
            player.addListener(listener)

            val mediaItem = MediaItem.fromUri(Uri.parse(url))

            player.setMediaItem(mediaItem)
            player.prepare()
            onPrepared()
        } catch (e: Exception) {
            Timber.e("Failed to initialize player", e)
            onInitializeFail(e)
        }
    }

    fun stop() {
        try {
            player.stop()
        } catch (e: UninitializedPropertyAccessException) {
            Timber.w("Called stop too early (ignoring): ${e.message}")
        }
    }

    fun play() {
        when (player.isPlaying) {
            true -> {
                player.pause()
                stopPositionCheck()
            }
            false -> {
                player.play()
                startPositionCheck()
            }
        }
    }

    fun skipForward() {
        player.seekForward()
    }

    fun skipBack() {
        player.seekBack()
    }

    fun getDuration(): Long {
        var duration = 0L

        // For some reason the tests tend to call this from outside the Main thread, this
        // ensures we are on the main thread to access player.duration
        if (Looper.myLooper() == Looper.getMainLooper()) {
            duration = player.duration
        } else {
            Handler(Looper.getMainLooper()).post { duration = player.duration }
        }

        return duration
    }

    private fun startPositionCheck() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && player.isPlaying) {
                val position = player.currentPosition.toInt()
                val bufferedPercentage = player.bufferedPercentage
                withContext(Dispatchers.IO) {
                    onPositionChange(position, bufferedPercentage)
                    delay(25L)
                }
            }
        }
    }

    private fun stopPositionCheck() {
        job?.cancel()
        job = null
    }
}