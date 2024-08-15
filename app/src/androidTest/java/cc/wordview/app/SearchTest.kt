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

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.ui.screens.home.Search
import cc.wordview.app.ui.screens.home.model.SearchViewModel
import cc.wordview.app.ui.theme.WordViewTheme
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamType

class SearchTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setupScreen() {
        composeTestRule.setContent {
            WordViewTheme { Search(rememberNavController()) }
        }
    }

    private fun getMockSearchResults(count: Int): ArrayList<StreamInfoItem> {
        val mockSearchResults = ArrayList<StreamInfoItem>()

        for (i in 0..count) {
            val video = StreamInfoItem(
                0,
                "https://youtube.com/watch?v=Yw6u6YkTgQ4",
                "$i hello world",
                StreamType.VIDEO_STREAM
            )
            video.uploaderName = "$i Louie Zong"

            mockSearchResults.add(video)
        }

        return mockSearchResults
    }

    @Test
    fun renders() {
        setupScreen()
        composeTestRule.onNodeWithTag("search-bar").assertExists()
    }

    @Test
    fun focusWhenNoResults() {
        SearchViewModel.setSearchResults(listOf())
        setupScreen()
        composeTestRule.waitUntil { SearchViewModel.searching.value }
        assertTrue(SearchViewModel.searching.value)
    }

    @Test
    fun noFocusWhenResults() {
        SearchViewModel.setSearchResults(getMockSearchResults(2))
        setupScreen()
        composeTestRule.waitUntil { !SearchViewModel.searching.value }
        assertFalse(SearchViewModel.searching.value)
    }

    @Test
    fun searchResultsRendering() {
        SearchViewModel.setSearchResults(getMockSearchResults(10))
        setupScreen()

        for (i in 0..10) {
            composeTestRule.onNodeWithText("$i hello world").assertExists()
            composeTestRule.onNodeWithText("$i Louie Zong").assertExists()
        }
    }
}