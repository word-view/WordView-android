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

package cc.wordview.app.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.ComposeTest
import cc.wordview.app.database.entity.ViewedVideo
import cc.wordview.app.ui.components.ContinueWatchingCard
import cc.wordview.app.ui.theme.WordViewTheme
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ContinueWatchingCardTest : ComposeTest() {
    private fun setup(onClick: Runnable) {
        composeTestRule.setContent {
            WordViewTheme {
                ProvidePreferenceLocals {
                    ContinueWatchingCard(
                        onClick = { onClick.run() },
                        viewedVideo = ViewedVideo(
                            id = "6gluNoLVKiQ",
                            title = "Eleanor Rigby",
                            artist = "The Beatles",
                            thumbnailUrl = "https://i.ytimg.com/vi_webp/6gluNoLVKiQ/maxresdefault.webp",
                            duration = 132,
                        )
                    )
                }
            }
        }
    }

    @Test
    fun renders() {
        setup(onClick = {})

        composeTestRule.onNodeWithText("Eleanor Rigby")
            .assertExists()
        composeTestRule.onNodeWithText("The Beatles")
            .assertExists()

        composeTestRule.onNodeWithTag("remote-image", useUnmergedTree = true)
            .assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun triggersOnClick() {
        val onClickMock: Runnable = mock()
        setup(onClick = onClickMock)
        composeTestRule.onNode(hasClickAction()).performClick()
        verify(onClickMock).run()
    }
}