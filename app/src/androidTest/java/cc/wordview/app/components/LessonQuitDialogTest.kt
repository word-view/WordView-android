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

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.components.LessonQuitDialog
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class LessonQuitDialogTest : ComposeTest() {
    private val onDismiss = mock(Runnable::class.java)
    private val onConfirm = mock(Runnable::class.java)

    @Before
    fun setup() {
        composeTestRule.setContent {
            LessonQuitDialog(
                onDismiss = { onDismiss.run() },
                onConfirm = { onConfirm.run() }
            )
        }
    }

    @Test
    fun confirm() {
        composeTestRule.onNodeWithText("Finish").performClick()
        verify(onConfirm).run()
    }

    @Test
    fun dismiss() {
        composeTestRule.onNodeWithText("Dismiss").performClick()
        verify(onDismiss).run()
    }
}