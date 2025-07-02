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

package cc.wordview.app.ui.activities.lesson

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.gengolex.Language
import timber.log.Timber
import kotlin.concurrent.thread

object ReviseTimer {
    private val viewModel = LessonViewModel

    var timeRemaining = 300000L

    private var timer: CountDownTimer? = null

    fun start(context: Context? = null, language: Language) {
        if (timer != null) {
            Timber.w("Timer is already running; The attempt to start will be ignored")
            return
        }

        Timber.i("Initializing timer with ${formatMillisecondsToMS(timeRemaining)} left")

        timer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // TODO: communicate the timer progress through a websocket
                timeRemaining = millisUntilFinished
                viewModel.setFormattedTime(formatMillisecondsToMS(millisUntilFinished))
            }

            override fun onFinish() {
                Timber.i("Timer finished!")
                viewModel.finishTimer(context, language)
            }
        }

        thread { timer?.start() }
    }

    fun pause() {
        Timber.i("Pausing timer with ${formatMillisecondsToMS(timeRemaining)} left")
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