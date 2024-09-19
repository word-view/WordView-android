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

package cc.wordview.app.ui.screens.home.revise.components

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel
import java.lang.Thread.sleep
import kotlin.concurrent.thread

object ReviseTimer {
    private val TAG = ReviseTimer::class.java.simpleName
    private val viewModel = WordReviseViewModel

    private var timeRemaining = 300000L

    private var timer: CountDownTimer? = null

    fun start() {
        Log.i(TAG, "Initializing timer with ${formatMillisecondsToMS(timeRemaining)} left")

        timer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // TODO: communicate the timer progress through a websocket
                timeRemaining = millisUntilFinished
                viewModel.setFormattedTime(formatMillisecondsToMS(millisUntilFinished))
            }

            override fun onFinish() {
                Log.i(TAG, "Timer finished!")
                viewModel.finishTimer()
            }
        }

        // wait a bit in case the transition to the reviser took a bit longer
        thread {
            sleep(1000)
            timer?.start()
        }
    }

    fun pause() {
        Log.i(TAG, "Pausing timer with ${formatMillisecondsToMS(timeRemaining)} left")
        timer?.cancel()
        timer = null
    }

    @SuppressLint("DefaultLocale")
    private fun formatMillisecondsToMS(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}