package cc.wordview.app.components

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import cc.wordview.app.ComposeTest
import cc.wordview.app.components.ui.AsyncImagePlaceholders
import cc.wordview.app.ui.components.SongCard
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SongCardTest : ComposeTest() {
    private fun setup(
        artist: String,
        trackName: String,
        onClick: Runnable
    ) {
        composeTestRule.setContent {
            var textVisible by remember { mutableStateOf(false) }

            if (textVisible) Text("Text")

            SongCard(
                modifier = Modifier.testTag("song-card"),
                thumbnail = "",
                artist = artist,
                asyncImagePlaceholders = AsyncImagePlaceholders(
                    noConnectionWhite = R.drawable.nonet,
                    noConnectionDark = R.drawable.nonet_dark
                ),
                duration = 120,
                trackName = trackName
            ) {
                textVisible = true
                onClick.run()
            }
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

        composeTestRule.onNodeWithText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut accumsan arcu ut fermentum viverra. Nulla et nulla ante. Donec vitae sem ac arcu maximus viverra vel vel neque")
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("song-card").assertWidthIsEqualTo(140.dp)
    }

    @Test
    fun displaysTrackBigString() {
        setup(
            trackName = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut accumsan arcu ut fermentum viverra. Nulla et nulla ante. Donec vitae sem ac arcu maximus viverra vel vel neque",
            artist = "Test Artist",
            onClick = {})

        composeTestRule.onNodeWithText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut accumsan arcu ut fermentum viverra. Nulla et nulla ante. Donec vitae sem ac arcu maximus viverra vel vel neque")
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("song-card").assertWidthIsEqualTo(140.dp)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun triggersOnClick() {
        val onClickMock: Runnable = mock()
        setup(artist = "Test Artist", trackName = "Test Track", onClick = onClickMock)
        composeTestRule.onNode(hasClickAction()).performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasText("Text"), 2_000)
        verify(onClickMock).run()
    }
}