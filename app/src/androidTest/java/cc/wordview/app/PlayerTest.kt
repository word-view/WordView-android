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
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.api.Video
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.app.ui.screens.home.Player
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import cc.wordview.app.ui.theme.WordViewTheme
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.util.ArrayList

class PlayerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setupScreen() {
        composeTestRule.setContent {
            WordViewTheme {
                ProvidePreferenceLocals {
                    Player(rememberNavController(), autoplay = false)
                }
            }
        }
    }

    private fun getMockSong(): Video {
        return Video("LfephiFN76E", "No Title", "REOL", "")
    }

    @Test
    fun renders() {
        SongViewModel.setVideo(getMockSong())
        setupScreen()
        composeTestRule.waitUntil(15_000) { PlayerViewModel.lyrics.value.size > 0 }
        composeTestRule.onNodeWithTag("back-button").assertExists()
        composeTestRule.onNodeWithText("No Title").assertExists()
        composeTestRule.onNodeWithTag("play").assertExists()
        composeTestRule.onNodeWithTag("skip-back").assertExists()
        composeTestRule.onNodeWithTag("skip-forward").assertExists()
    }

    @Test
    fun performGoBack() {
        SongViewModel.setVideo(getMockSong())
        setupScreen()
        composeTestRule.waitUntil(15_000) { PlayerViewModel.lyrics.value.size > 0 }
        composeTestRule.onNodeWithTag("back-button").performClick()
    }

    @Test
    fun loaderWhenNoLyrics() {
        PlayerViewModel.setCues(arrayListOf())
        setupScreen()
        composeTestRule.onNodeWithTag("async-composable-progress").assertExists()
    }

    @Test
    fun lyricsRenderCorrectly() {
        val mockLyrics = ArrayList<WordViewCue>()

        for (i in 1..20) {
            mockLyrics.add(WordViewCue("$i hello world", 1000 * i, 2000 * i))
        }

        PlayerViewModel.setCues(mockLyrics)

        setupScreen()

        for (cue in PlayerViewModel.lyrics.value) {
            PlayerViewModel.setCurrentCue(cue)
            composeTestRule.onNodeWithText(cue.text).assertExists()
        }
    }

    @Test
    @Ignore("Works individually but hangs when it is run along with other tests")
    fun playButtonWorks() {
        SongViewModel.setVideo(getMockSong())
        setupScreen()
        composeTestRule.waitUntil(60_000) { PlayerViewModel.lyrics.value.size > 0 }
        composeTestRule.onNodeWithTag("play").performClick()
//        composeTestRule.waitUntil(10_000) { PlayerViewModel.highlightedCuePosition.value != 0 }
    }

    @Test
    @Ignore("Works individually but hangs when it is run along with other tests")
    fun skipForwardButtonWorks() {
        SongViewModel.setVideo(getMockSong())
        setupScreen()
        composeTestRule.waitUntil(60_000) { PlayerViewModel.lyrics.value.size > 0 }
        composeTestRule.onNodeWithTag("skip-forward").performClick().performClick().performClick()
        composeTestRule.onNodeWithTag("play").performClick()
//        composeTestRule.waitUntil(10_000) { PlayerViewModel.highlightedCuePosition.value > 15000 }
    }

    @Test
    @Ignore("Works individually but hangs when it is run along with other tests")
    fun skipBackButtonWorks() {
        SongViewModel.setVideo(getMockSong())
        setupScreen()
        composeTestRule.waitUntil(60_000) { PlayerViewModel.lyrics.value.size > 0 }
        composeTestRule.onNodeWithTag("skip-forward").performClick().performClick().performClick()
        composeTestRule.onNodeWithTag("skip-back").performClick().performClick().performClick()
        composeTestRule.onNodeWithTag("play").performClick()
//        composeTestRule.waitUntil(10_000) { PlayerViewModel.highlightedCuePosition.value < 15000 }
    }
}