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

package cc.wordview.app.ui.screens.search

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import cc.wordview.app.MainActivity
import cc.wordview.app.ui.activities.home.composables.search.SearchRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class SearchTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var searchRepository: SearchRepository

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.onNodeWithTag("search").performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun search() {
        composeTestRule.onNodeWithTag("search-input-field").performTextInput("single")
        composeTestRule.onNodeWithTag("search-input-field").performImeAction()
        composeTestRule.waitUntilNodeCount(hasTestTag("result-item"), 1, 20_000)
        composeTestRule.onNodeWithText("No Title").assertExists()
        composeTestRule.onNodeWithText("REOL").assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun searchMultipleResults() {
        composeTestRule.onNodeWithTag("search-input-field").performTextInput("multi")
        composeTestRule.onNodeWithTag("search-input-field").performImeAction()
        composeTestRule.waitUntilNodeCount(hasTestTag("result-item"), 3, 20_000)

        composeTestRule.onNodeWithText("No Title").assertExists()
        composeTestRule.onNodeWithText("REOL").assertExists()

        composeTestRule.onNodeWithText("Hibana").assertExists()
        composeTestRule.onNodeWithText("DECO*27").assertExists()

        composeTestRule.onNodeWithText("Readymade").assertExists()
        composeTestRule.onNodeWithText("Ado").assertExists()
    }

    @Test
    fun searchNoNet() {
        composeTestRule.onNodeWithTag("search-input-field").performTextInput("nonet")
        composeTestRule.onNodeWithTag("search-input-field").performImeAction()
        composeTestRule.onNodeWithTag("error").assertExists()
    }
}