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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.api.VideoSearchResult
import cc.wordview.app.ui.components.ResultItem
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.schabi.newpipe.extractor.Image
import kotlin.time.Duration.Companion.seconds

class ResultItemTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setup(result: VideoSearchResult, onClick: Runnable) {
        composeTestRule.setContent {
            ResultItem(result = result, onClick = { onClick.run() })
        }
    }

    @Test
    fun displayVerified() {
        val result = createVideoSearchResult(
            title = "Sample Video",
            channel = "Sample Verified Channel",
            duration = 123,
            isVerified = true,
        )

        assertWorksAndDisplayCorrectly(result)
    }

    @Test
    fun displayNotVerified() {
        val result = createVideoSearchResult(
            title = "Sample Video",
            channel = "Sample Unverified Channel",
            duration = 123,
            isVerified = false,
        )

        assertWorksAndDisplayCorrectly(result)
    }

    @Test
    fun displayBigDuration() {
        val result = createVideoSearchResult(
            title = "Sample Big Duration Video",
            channel = "Sample Channel",
            duration = 1600,
            isVerified = false,
        )

        assertWorksAndDisplayCorrectly(result)
    }

    @Test
    fun displaySmallDuration() {
        val result = createVideoSearchResult(
            title = "Sample Small Duration Video",
            channel = "Sample Channel",
            duration = 10,
            isVerified = false,
        )

        assertWorksAndDisplayCorrectly(result)
    }

    private fun assertWorksAndDisplayCorrectly(result: VideoSearchResult) {
        val onClick = mock(Runnable::class.java)
        setup(result, onClick)

        assertResultItemDisplaysContent(result)
        composeTestRule.onNodeWithTag("result-item").performClick()
        verify(onClick).run()
    }

    private fun assertResultItemDisplaysContent(result: VideoSearchResult) {
        composeTestRule.onNodeWithText(result.title)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(result.channel)
            .assertExists()
            .assertIsDisplayed()

        if (result.channelIsVerified) {
            composeTestRule.onNodeWithContentDescription("Verified")
                .assertExists()
        }

        composeTestRule.onNodeWithText(result.duration.seconds.toString())
            .assertExists()
            .assertIsDisplayed()
    }

    private fun createVideoSearchResult(
        id: String = "1",
        title: String = "Sample Video",
        channel: String = "Sample Channel",
        duration: Long = 120,
        isVerified: Boolean = true,
        thumbnailUrl: String = ""
    ): VideoSearchResult {
        return VideoSearchResult(
            id = id,
            title = title,
            channel = channel,
            duration = duration,
            thumbnails = listOf(Image(thumbnailUrl, 32, 32, Image.ResolutionLevel.LOW)),
            channelIsVerified = isVerified
        )
    }
}