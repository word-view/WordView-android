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

package cc.wordview.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.ui.screens.home.Home
import cc.wordview.app.ui.screens.home.LearnTab
import cc.wordview.app.ui.screens.home.Tabs
import cc.wordview.app.ui.theme.WordViewTheme
import org.junit.Rule
import org.junit.Test

class HomeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun learnTabRendersByItself() {
       composeTestRule.setContent {
           val mockNavController = rememberNavController()
           val mockNavHostController = rememberNavController()

           WordViewTheme {
               LearnTab(navController = mockNavController, navHostController = mockNavHostController)
           }
       }

        composeTestRule.onAllNodesWithTag("song-card").assertCountEquals(3)
    }
    
    @Test
    fun navigateBetweenTabs() {
        composeTestRule.setContent { 
            val mockNavController = rememberNavController()
            
            WordViewTheme {
                Home(navHostController = mockNavController)
            }
        }

        performTabTest(Tabs.Profile.route)
        performTabTest(Tabs.Explore.route)
        performTabTest(Tabs.Learn.route)
    }

    private fun performTabTest(tabRoute: String) {
        composeTestRule.onNodeWithTag("$tabRoute-tab").assertExists()
        composeTestRule.onNodeWithTag("$tabRoute-tab").performTouchInput { click() }
    }

}