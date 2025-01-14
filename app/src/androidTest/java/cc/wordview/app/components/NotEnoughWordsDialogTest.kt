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

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.ui.components.NotEnoughWordsDialog
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class NotEnoughWordsDialogTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val onConfirm = mock(Runnable::class.java)

    @Before
    fun setup() {
        composeTestRule.setContent {
            NotEnoughWordsDialog(
                onConfirm = { onConfirm.run() }
            )
        }
    }

    @Test
    fun confirm() {
        composeTestRule.onNodeWithText("Go back").performClick()
        verify(onConfirm).run()
    }
}