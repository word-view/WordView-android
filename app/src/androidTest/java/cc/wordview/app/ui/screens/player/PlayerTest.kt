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

package cc.wordview.app.ui.screens.player

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.MainActivity
import cc.wordview.app.SongViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class PlayerTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var playerRepository: PlayerRepository

    @Before
    fun setup() {
        hiltRule.inject()

        SongViewModel.setVideoStream(MockVideoStream())

        composeTestRule.onNodeWithText("Aquarela").performClick()
    }

    @Test
    fun renders() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("interface"), 5_000)

        composeTestRule.onNodeWithText("Gran Vals").assertExists()
        composeTestRule.onNodeWithText("Francisco Tárrega").assertExists()
        composeTestRule.onNodeWithTag("back-button").assertExists()

        composeTestRule.onNodeWithTag("skip-back").assertExists()
        composeTestRule.onNodeWithTag("toggle-play").assertExists()
        composeTestRule.onNodeWithTag("skip-forward").assertExists()
        composeTestRule.onNodeWithTag("seekbar", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("progress-line", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("buffer-line", useUnmergedTree = true).assertExists()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-plain"), 5_000)
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-word"), 5_000)
    }

    @Test
    fun goBack() {
        composeTestRule.waitUntilNodeCount(hasTestTag("interface"), 1, 5_000)
        composeTestRule.onNodeWithTag("back-button").performClick()

        SongViewModel.setVideoStream(MockVideoStream())

        composeTestRule.onNodeWithText("Aquarela").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("interface"), 5_000)
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-plain"), 5_000)
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-word"), 5_000)
    }

    @Ignore("Unstable results")
    @Test
    fun pause() {
        composeTestRule.waitUntilNodeCount(hasTestTag("toggle-play"), 1, 1_000)
        composeTestRule.onNodeWithTag("toggle-play").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-plain"))
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-word"))
    }

    @Test
    fun skipForward() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("skip-forward"), 2_000)
        composeTestRule.onNodeWithTag("skip-forward")
            .performClick()
            .performClick()
            .performClick()
            .performClick()

        composeTestRule.onNodeWithTag("toggle-play").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-plain"))
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-word"))
    }


    @Test
    fun skipBack() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("skip-forward"), 2_000)
        composeTestRule.onNodeWithTag("skip-forward")
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()

        composeTestRule.onNodeWithTag("skip-back")
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-plain"), 15_000)
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-word"), 15_000)
    }
}