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

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.components.CircularProgressIndicator
import org.junit.Test

class CircularProgressIndicatorTest : ComposeTest() {
    private fun setup(size: Dp) {
        composeTestRule.setContent {
            CircularProgressIndicator(size)
        }
    }

    @Test
    fun renders64() {
        setup(64.dp)
        composeTestRule.onNodeWithTag("loader")
            .assertExists()
            .assertIsDisplayed()
            .assertWidthIsEqualTo(64.dp)
            .assertHeightIsEqualTo(64.dp)
    }

    @Test
    fun renders32() {
        setup(32.dp)
        composeTestRule.onNodeWithTag("loader")
            .assertExists()
            .assertIsDisplayed()
            .assertWidthIsEqualTo(32.dp)
            .assertHeightIsEqualTo(32.dp)
    }

    @Test
    fun renders16() {
        setup(16.dp)
        composeTestRule.onNodeWithTag("loader")
            .assertExists()
            .assertIsDisplayed()
            .assertWidthIsEqualTo(16.dp)
            .assertHeightIsEqualTo(16.dp)
    }
}