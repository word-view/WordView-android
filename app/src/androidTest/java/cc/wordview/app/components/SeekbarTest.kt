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
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import cc.wordview.app.ui.components.Seekbar
import org.junit.Rule
import org.junit.Test

class SeekbarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setup(currentPosition: Long, duration: Long, bufferingProgress: Int) {
        composeTestRule.setContent {
            Seekbar(currentPosition = currentPosition, duration = duration, bufferingProgress = bufferingProgress)
        }
    }

    @Test
    fun renders() {
        setup(5_000, 300_000, 50)
        composeTestRule.onNodeWithText("00:05 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertExists()
        composeTestRule.onNodeWithTag("buffer-line").assertExists()
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(457.dp)
    }

    @Test
    fun positionsZero() {
        setup(0, 0, 0)
        composeTestRule.onNodeWithText("0:00 / 0:00").assertExists()
    }

    // progress width tests
    @Test
    fun progress100() {
        setup(300_000, 300_000, 50)
        composeTestRule.onNodeWithText("05:00 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(914.dp)
    }

    @Test
    fun progress75() {
        setup(225_000, 300_000, 50)
        composeTestRule.onNodeWithText("03:45 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(686.dp)
    }

    @Test
    fun progress50() {
        setup(150_000, 300_000, 50)
        composeTestRule.onNodeWithText("02:30 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(457.dp)
    }

    @Test
    fun progress25() {
        setup(75_000, 300_000, 50)
        composeTestRule.onNodeWithText("01:15 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(229.dp)
    }

    @Test
    fun progress10() {
        setup(30_000, 300_000, 50)
        composeTestRule.onNodeWithText("00:30 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(91.dp)
    }

    @Test
    fun progress5() {
        setup(15_000, 300_000, 50)
        composeTestRule.onNodeWithText("00:15 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(46.dp)
    }

    @Test
    fun progress1() {
        setup(3_000, 300_000, 50)
        composeTestRule.onNodeWithText("00:03 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(9.dp)
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

    // buffer width tests
    @Test
    fun bufferAt100() {
        setup(0, 0, 100)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(914.dp)
    }

    @Test
    fun bufferAt75() {
        setup(0, 0, 75)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(686.dp)
    }

    @Test
    fun bufferAt50() {
        setup(0, 0, 50)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(457.dp)
    }

    @Test
    fun bufferAt25() {
        setup(0, 0, 25)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(229.dp)
    }

    @Test
    fun bufferAt10() {
        setup(0, 0, 10)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(91.dp)
    }

    @Test
    fun bufferAt5() {
        setup(0, 0, 5)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(46.dp)
    }

    @Test
    fun bufferAt1() {
        setup(0, 0, 1)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(9.dp)
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