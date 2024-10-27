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

package cc.wordview.app.audio

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AudioPlayer {
    private val TAG = this::class.java.simpleName

    private lateinit var player: ExoPlayer

    private var job: Job? = null

    var onPositionChange: (Int) -> Unit = {}
    var onPrepared: () -> Unit = {}
    var onInitializeFail: (Exception) -> Unit = {}

    fun initialize(url: String, context: Context, listener: AudioPlayerListener) {
        Log.i(TAG, "Streaming from $url")

        try {
            player = ExoPlayer.Builder(context).setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(), true
            ).build()

            listener.onBuffering = { stopPositionCheck() }
            listener.onReady = { startPositionCheck() }

            player.addListener(listener)

            val mediaItem = MediaItem.fromUri(Uri.parse(url))

            player.setMediaItem(mediaItem)
            player.prepare()
            onPrepared()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize player", e)
            onInitializeFail(e)
        }
    }

    fun stop() {}

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


    private fun startPositionCheck() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && player.isPlaying) {
                val position = player.currentPosition.toInt()
                withContext(Dispatchers.IO) {
                    onPositionChange(position)
                    delay(1L)
                }
            }
        }
    }

    private fun stopPositionCheck() {
        job?.cancel()
        job = null
    }
}