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
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.components.SearchHistoryEntry
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class SearchHistoryEntryTest : ComposeTest() {
    private fun setup(onClick: Runnable, onLongClick: Runnable) {
        composeTestRule.setContent {
            SearchHistoryEntry(
                entry = "sample search entry",
                onClick = { onClick.run() },
                onLongClick = { onLongClick.run() }
            )
        }
    }

    @Test
    fun renders() {
        setup(onClick = {}, onLongClick = {})
        composeTestRule.onNodeWithText("sample search entry")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun onClickWorks() {
        val onClick = mock(Runnable::class.java)

        setup(onClick = onClick, onLongClick = {})

        composeTestRule.onNodeWithText("sample search entry")
            .performClick()
            .performClick()
            .performClick()
            .performClick()

        verify(onClick, times(4)).run()
    }

    @Test
    fun onLongClickWorks() {
        val onLongClick = mock(Runnable::class.java)

        setup(onClick = {}, onLongClick = onLongClick)

        composeTestRule.onNodeWithText("sample search entry")
            .performTouchInput { longClick() }

        verify(onLongClick, times(1)).run()
    }
}