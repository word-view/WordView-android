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

package cc.wordview.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import cc.wordview.app.ui.components.OneTimeEffect
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class OneTimeEffectTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockBlock = mock(Runnable::class.java)

    @Before
    fun setup() {
        composeTestRule.setContent {
            var topPadding by mutableStateOf(1)

            Column(Modifier.padding(start = topPadding.dp)) {
                OneTimeEffect { mockBlock.run() }

                Button(onClick = { topPadding += 10 }) {
                    Text("Recompose")
                }
            }
        }
    }

    @Test
    fun runsOnlyOnce() {
        verify(mockBlock, times(1)).run()
        composeTestRule.onNodeWithText("Recompose").performClick()
        verify(mockBlock, times(1)).run()
        composeTestRule.onNodeWithText("Recompose").performClick()
        verify(mockBlock, times(1)).run()
        composeTestRule.onNodeWithText("Recompose").performClick()
        verify(mockBlock, times(1)).run()
        composeTestRule.onNodeWithText("Recompose").performClick()
        verify(mockBlock, times(1)).run()
        composeTestRule.onNodeWithText("Recompose").performClick()
        verify(mockBlock, times(1)).run()
    }
}