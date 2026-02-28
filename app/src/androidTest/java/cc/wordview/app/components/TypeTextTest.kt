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

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.components.TypeText
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class TypeTextTest : ComposeTest() {
    @Test
    fun rendersWord_English() {
        composeTestRule.setContent { TypeText(text = "World") }
        composeTestRule.onNodeWithTag("type-text").assertExists()
        composeTestRule.waitUntilAtLeastOneExists(hasText("World"), 2_000)
    }

    @Test
    fun rendersWord_Portuguese() {
        composeTestRule.setContent { TypeText(text = "Mundo") }
        composeTestRule.onNodeWithTag("type-text").assertExists()
        composeTestRule.waitUntilAtLeastOneExists(hasText("Mundo"), 2_000)
    }

    @Test
    fun rendersWord_Japanese() {
        composeTestRule.setContent { TypeText(text = "世界") }
        composeTestRule.onNodeWithTag("type-text").assertExists()
        composeTestRule.waitUntilAtLeastOneExists(hasText("世界"), 2_000)
    }

    @Test
    fun rendersLongWord_English() {
        composeTestRule.setContent { TypeText(text = "Ingredients") }
        composeTestRule.onNodeWithTag("type-text").assertExists()
        composeTestRule.waitUntilAtLeastOneExists(hasText("Ingredients"), 2_000)
    }

    @Test
    fun rendersLongWord_Portuguese() {
        composeTestRule.setContent { TypeText(text = "Ingredientes") }
        composeTestRule.onNodeWithTag("type-text").assertExists()
        composeTestRule.waitUntilAtLeastOneExists(hasText("Ingredientes"), 2_000)
    }

    @Test
    fun rendersLongWord_Japanese() {
        composeTestRule.setContent { TypeText(text = "ゴミ収集車") }
        composeTestRule.onNodeWithTag("type-text").assertExists()
        composeTestRule.waitUntilAtLeastOneExists(hasText("ゴミ収集車"), 2_000)
    }
}