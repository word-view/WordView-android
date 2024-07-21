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
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.ui.screens.home.Search
import cc.wordview.app.ui.screens.home.model.SearchViewModel
import cc.wordview.app.ui.theme.WordViewTheme
import org.junit.Rule
import org.junit.Test
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamType

class SearchTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun getMockSearchResults(): ArrayList<StreamInfoItem> {
        val mockSearchResults = ArrayList<StreamInfoItem>()

        for (i in 0..20) {
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
    fun searchResultsRendering() {
        SearchViewModel.setSearchResults(getMockSearchResults())

        composeTestRule.setContent {
            WordViewTheme { Search(navController = rememberNavController()) }
        }

        for (i in 0..20) {
            composeTestRule.onNodeWithText("$i hello world").assertExists()
            composeTestRule.onNodeWithText("$i Louie Zong").assertExists()
        }
    }
}