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
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.components.Space
import org.junit.Test

class SpaceTest : ComposeTest() {
    @Test
    fun dimensionsMatchRequested_4dp() = testDimensionsMatch(4.dp)

    @Test
    fun dimensionsMatchRequested_8dp() = testDimensionsMatch(8.dp)

    @Test
    fun dimensionsMatchRequested_24dp() = testDimensionsMatch(24.dp)

    @Test
    fun dimensionsMatchRequested_32dp() = testDimensionsMatch(32.dp)

    @Test
    fun dimensionsMatchRequested_64dp() = testDimensionsMatch(64.dp)

    private fun testDimensionsMatch(requested: Dp) {
        composeTestRule.setContent { Space(requested) }

        composeTestRule.onNodeWithTag("space").assertWidthIsEqualTo(requested)
        composeTestRule.onNodeWithTag("space").assertHeightIsEqualTo(requested)
    }
}