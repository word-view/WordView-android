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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import cc.wordview.app.ui.components.PlayerButton
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class PlayerButtonTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setup(onClick: Runnable) {
        composeTestRule.setContent {
            var icon by mutableStateOf(Icons.Filled.PlayArrow)

            PlayerButton(
                modifier = Modifier.testTag("button"),
                icon = icon,
                size = 64.dp,
                onClick = {
                    icon =
                        if (icon == Icons.Filled.PlayArrow) Icons.Filled.Pause else Icons.Filled.PlayArrow
                    onClick.run()
                })
        }
    }

    @Test
    fun sizeCorrect() {
        setup(mock(Runnable::class.java))
        composeTestRule.onNodeWithTag("button").assertHeightIsEqualTo(64.dp)
        composeTestRule.onNodeWithTag("button").assertWidthIsEqualTo(64.dp)
    }

    @Test
    fun press() {
        val onClick = mock(Runnable::class.java)
        setup(onClick)
        composeTestRule.onNodeWithTag("button").performClick()
        composeTestRule.onNodeWithTag("button").performClick()
        composeTestRule.onNodeWithTag("button").performClick()
        composeTestRule.onNodeWithTag("button").performClick()
        verify(onClick, times(4)).run()
    }
}