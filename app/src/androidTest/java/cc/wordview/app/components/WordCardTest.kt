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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.components.WordCard
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class WordCardTest : ComposeTest() {
    private val onClick = mock(Runnable::class.java)

    private fun setup(onClick: Runnable) {
        composeTestRule.setContent {
            WordCard(text = "Hello", onClick = { onClick.run() })
        }
    }

    @Test
    fun press() {
        setup(onClick = onClick)

        composeTestRule.onNodeWithText("Hello")
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        verify(onClick).run()
    }
}