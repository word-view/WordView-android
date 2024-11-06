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

package cc.wordview.app.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import cc.wordview.app.ui.components.SongCard
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SongCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setup(artist: String, trackName: String, onClick: Runnable) {
        composeTestRule.setContent {
            SongCard(modifier = Modifier.testTag("song-card"), thumbnail = "", artist = artist, trackName = trackName) { onClick.run() }
        }
    }

    @Test
    fun displaysArtistAndTrack() {
        setup(artist = "Test Artist", trackName = "Test Track", onClick = {})

        composeTestRule.onNodeWithText("Test Artist").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Track").assertIsDisplayed()
    }

    @Test
    fun displaysArtistBigString() {
        setup(
            artist = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut accumsan arcu ut fermentum viverra. Nulla et nulla ante. Donec vitae sem ac arcu maximus viverra vel vel neque",
            trackName = "Test Track",
            onClick = {})

        composeTestRule.onNodeWithText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut accumsan arcu ut fermentum viverra. Nulla et nulla ante. Donec vitae sem ac arcu maximus viverra vel vel neque").assertIsDisplayed()
        composeTestRule.onNodeWithTag("song-card").assertWidthIsEqualTo(140.dp)
    }

    @Test
    fun displaysTrackBigString() {
        setup(
            trackName = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut accumsan arcu ut fermentum viverra. Nulla et nulla ante. Donec vitae sem ac arcu maximus viverra vel vel neque",
            artist = "Test Artist",
            onClick = {})

        composeTestRule.onNodeWithText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut accumsan arcu ut fermentum viverra. Nulla et nulla ante. Donec vitae sem ac arcu maximus viverra vel vel neque").assertIsDisplayed()
        composeTestRule.onNodeWithTag("song-card").assertWidthIsEqualTo(140.dp)
    }

    @Test
    fun triggersOnClick() {
        val onClickMock: Runnable = mock()
        setup(artist = "Test Artist", trackName = "Test Track", onClick = onClickMock)
        composeTestRule.onNode(hasClickAction()).performClick()
        verify(onClickMock).run()
    }
}