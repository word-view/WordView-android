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

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.activities.home.composables.home.Home
import cc.wordview.app.ui.activities.home.composables.home.LearnTab
import cc.wordview.app.ui.activities.home.composables.home.Tabs
import org.junit.Test

class HomeTest : ComposeTest() {
    @Test
    fun learnTabRendersByItself() {
        composeTestRule.setContent {
            LearnTab()
        }

        composeTestRule.onAllNodesWithTag("song-card").assertCountEquals(3)
    }

    @Test
    fun fullHomeRenders() {
        composeTestRule.setContent {
            val mockNavController = rememberNavController()
            Home(navHostController = mockNavController)
        }

        composeTestRule.onAllNodesWithTag("song-card").assertCountEquals(3)
    }
}