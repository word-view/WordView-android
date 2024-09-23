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

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayer : MediaPlayer() {
    private val TAG = AudioPlayer::class.java.simpleName

    private var state = AudioPlayerState.STALE

    private var job: Job? = null

    var onPositionChange: (Int) -> Unit = {}
    var onPrepared: () -> Unit = {}
    var onInitializeFail: (Exception) -> Unit = {}

    init {
        setOnPreparedListener {
            Log.d(TAG, "Audio is prepared")
            state = AudioPlayerState.INITIALIZED
            onPrepared()
        }

        setOnBufferingUpdateListener { _, percent ->
            Log.d(TAG, "Buffering $percent%")
        }
    }

    fun initialize(dataSource: String, onComplete: () -> Unit = {}) {
        Log.d(TAG, "Initializing MediaPlayer with dataSource: $dataSource")

        if (state == AudioPlayerState.INITIALIZED) {
            state = AudioPlayerState.STALE
            reset()
        }

        try {
            this.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(dataSource)
            }
            prepare()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            onInitializeFail(e)
        }
    }

    override fun stop() {
        if (state == AudioPlayerState.INITIALIZED) super.stop()
        PlayerViewModel.playIconPause()

        stopPositionCheck()
        this.reset()
    }

    fun togglePlay() {
        if (state == AudioPlayerState.STALE) return

        try {
            if (this.isPlaying) {
                pause()
                stopPositionCheck()
                PlayerViewModel.playIconPause()
            } else {
                start()
                startPositionCheck()
                PlayerViewModel.playIconPlay()
            }
        } catch (e: IllegalStateException) {
            e.message?.let { Log.w(TAG, it) }
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

    private fun startPositionCheck() {
        Log.d(TAG, "Starting position check")
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive && isPlaying) {
                onPositionChange(currentPosition)
                delay(1L)
            }
        }
    }

    private fun stopPositionCheck() {
        Log.d(TAG, " position check")
        job?.cancel()
        job = null
    }

    fun getState(): AudioPlayerState {
        return this.state
    }
}