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

package cc.wordview.app.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import cc.wordview.app.ComposeTest
import cc.wordview.app.GlobalViewModel
import cc.wordview.app.api.entity.User
import cc.wordview.app.ui.activities.home.composables.Profile
import org.junit.Before
import org.junit.Test

class ProfileTest : ComposeTest() {
    @Before
    fun setup() {
        GlobalViewModel.setUser(User(
            id = "1",
            username = "Mock user",
            email = "mock.user@gmail.com"
        ))
    }

    @Test
    fun renders() {
        composeTestRule.setContent { Profile() }

        composeTestRule.onNodeWithText("Mock user")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("mock.user@gmail.com")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("logout")
            .assertExists()
            .assertIsDisplayed()
    }
}