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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithTag
import cc.wordview.app.ComposeTest
import cc.wordview.app.hasAlpha
import cc.wordview.app.ui.components.FlashingBall
import org.junit.Test

class FlashingBallTest : ComposeTest() {
    @Test
    fun flashes() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            FlashingBall(
                color = Color(0xFFFFFF00),
                delayTime = 500
            )
        }

        composeTestRule.onNodeWithTag("flashing-ball")
            .assertExists()
            .assert(hasAlpha(0f))

        composeTestRule.mainClock.advanceTimeBy(1150)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("flashing-ball")
            .assertExists()
            .assert(hasAlpha(0.3f))

        composeTestRule.mainClock.advanceTimeBy(420)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("flashing-ball")
            .assertExists()
            .assert(hasAlpha(0f))

    }
}