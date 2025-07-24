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

package cc.wordview.app.components

import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.components.Seekbar
import org.junit.Test

class SeekbarTest : ComposeTest() {
    private fun setup(currentPosition: Long, duration: Long, bufferingProgress: Int) {
        composeTestRule.setContent {
            Seekbar(currentPosition = currentPosition, duration = duration, bufferingProgress = bufferingProgress)
        }
    }

    @Test
    fun positionsZero() {
        setup(0, 0, 0)
        composeTestRule.onNodeWithText("0:00 / 0:00").assertExists()
    }

    @Test
    fun progress0() {
        setup(0, 300_000, 50)
        composeTestRule.onNodeWithText("0:00 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(0.dp)
    }

    @Test
    fun progressNegative() {
        setup(-2500, 300_000, 50)
        composeTestRule.onNodeWithText("0:00 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(0.dp)
    }

    @Test
    fun bufferAt0() {
        setup(0, 0, 0)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(0.dp)
    }

    @Test
    fun bufferNegative() {
        setup(0, 0, -10)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(0.dp)
    }
}