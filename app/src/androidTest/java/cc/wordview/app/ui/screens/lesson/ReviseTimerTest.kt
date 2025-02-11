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

package cc.wordview.app.ui.screens.lesson

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.junit4.createComposeRule
import cc.wordview.app.ui.screens.lesson.components.ReviseTimer
import org.junit.Rule
import org.junit.Test

class ReviseTimerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = LessonViewModel

    private fun setup(autoAdvance: Boolean = true, timeRemaining: Long = 1000L) {
        composeTestRule.mainClock.autoAdvance = autoAdvance
        composeTestRule.setContent {
            LaunchedEffect(Unit) {
                ReviseTimer.timeRemaining = timeRemaining
                ReviseTimer.start()
            }
        }
    }

    @Test
    fun timerFinishWorks() {
        setup()
        composeTestRule.waitUntil(5_000) { viewModel.timerFinished.value }
    }

    @Test
    fun ticksUpdateFormattedTime() {
        setup(true, 30_000)
        composeTestRule.waitUntil(5_000) { viewModel.timer.value == "0:29" }
    }
}