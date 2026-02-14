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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import cc.wordview.app.ComposeTest
import cc.wordview.app.misc.UserViewModel
import cc.wordview.app.api.entity.User
import cc.wordview.app.ui.activities.home.composables.ProfileScreen
import com.composegears.tiamat.compose.Navigation
import com.composegears.tiamat.compose.navigationPlatformDefault
import com.composegears.tiamat.compose.rememberNavController
import org.junit.Before
import org.junit.Test

class ProfileTest : ComposeTest() {
    @Before
    fun setup() {
        UserViewModel.setUser(User(
            id = "1",
            username = "Mock user",
            email = "mock.user@gmail.com"
        ))
    }

    @Test
    fun renders() {
        composeTestRule.setContent {
            val navController = rememberNavController(
                key = "testNavController",
                startDestination = ProfileScreen,
                configuration = {},
            )

            Navigation(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                contentTransformProvider = { navigationPlatformDefault(it) },
                destinations = arrayOf(ProfileScreen),
            )
        }

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