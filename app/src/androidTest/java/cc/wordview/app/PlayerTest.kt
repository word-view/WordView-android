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

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.api.Video
import cc.wordview.app.audio.AudioPlayerState
import cc.wordview.app.extractor.DownloaderImpl
import cc.wordview.app.ui.screens.home.Player
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.schabi.newpipe.extractor.NewPipe

class PlayerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = PlayerViewModel

    private fun setupScreen(autoplay: Boolean = false) {
        DownloaderImpl.init(null)
        NewPipe.init(DownloaderImpl.getInstance())

        composeTestRule.setContent {
            ProvidePreferenceLocals {
                Player(navHostController = rememberNavController(), autoplay = autoplay)
            }
        }
    }

    private fun getMockSong(): Video {
        return Video("LfephiFN76E", "No Title", "REOL", "")
    }

    @Test
    fun errorScreenRenders() {
        viewModel.setAudioInitFailed(true)
        setupScreen()
        composeTestRule.onNodeWithTag("error-screen").assertExists()
    }

    @Test
    fun playerInterfaceRenders() {
        viewModel.setAudioInitFailed(false)
        SongViewModel.setVideo(getMockSong())
        setupScreen(false)
        composeTestRule.waitUntil(40_000) { viewModel.player.value.getState() == AudioPlayerState.INITIALIZED }

        composeTestRule.onNodeWithTag("player-interface").assertExists()

        composeTestRule.onNodeWithTag("text-cue").assertExists()

        composeTestRule.onNodeWithTag("skip-back").assertExists()
        composeTestRule.onNodeWithTag("play").assertExists()
        composeTestRule.onNodeWithTag("skip-forward").assertExists()

        composeTestRule.onNodeWithTag("back-button").assertExists()
        composeTestRule.onNodeWithText(getMockSong().title).assertExists()
    }

    @Test
    @Ignore("Playing can cause issues with other tests so this one needs to be run individually")
    fun autoplayWorks() {
        viewModel.setAudioInitFailed(false)
        SongViewModel.setVideo(getMockSong())
        setupScreen(true)
        composeTestRule.waitUntil(40_000) { viewModel.player.value.isPlaying }
    }
}