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

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.api.Video
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.app.ui.screens.home.Player
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import cc.wordview.app.ui.theme.WordViewTheme
import org.junit.Rule
import org.junit.Test
import java.util.ArrayList

class PlayerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun renders() {
        SongViewModel.setVideo(Video("", "No Title", "REOL", ""))

        composeTestRule.setContent { Player() }

        composeTestRule.onNodeWithText("No Title").assertExists()
    }

    @Test
    fun loaderDisappearsWhenLyricsAreLoaded() {
        PlayerViewModel.setCues(ArrayList())
        val mockLyrics = arrayListOf(WordViewCue("hello world", 0, 1000))

        composeTestRule.setContent { Player() }

        composeTestRule.onNodeWithTag("lyrics-loader").assertExists()

        PlayerViewModel.setCues(mockLyrics)

        composeTestRule.onNodeWithTag("lyrics-loader").assertDoesNotExist()
        composeTestRule.onNodeWithText("hello world").assertExists()
    }

    @Test
    fun lyricsRenderCorrectly() {
        val mockLyrics = ArrayList<WordViewCue>()

        for (i in 1..20) {
            mockLyrics.add(WordViewCue("$i hello world", 1000 * i, 2000 * i))
        }

        PlayerViewModel.setCues(mockLyrics)

        composeTestRule.setContent { Player() }

        for (i in 1..20) {
            PlayerViewModel.highlightCueAt(1000 * i)
            composeTestRule.onNodeWithText("$i hello world").assertExists()
        }
    }

    @Composable
    fun Player() {
        WordViewTheme {
            Player(navController = rememberNavController())
        }
    }
}