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

package cc.wordview.app.ui.screens.lesson

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.ComposeTest
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.activities.lesson.LessonNav
import cc.wordview.app.ui.activities.lesson.composables.Choose
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.activities.lesson.viewmodel.ReviseWord
import cc.wordview.gengolex.word.Word
import coil3.request.ImageRequest
import coil3.request.allowHardware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Test

class ChooseTest : ComposeTest() {
    private val viewModel = LessonViewModel

    private fun setupScreen() {
        viewModel.appendWord(ReviseWord(Word("tear", "lágrima", representable = true)))
        viewModel.appendWord(ReviseWord(Word("rain", "chuva", representable = true)))
        viewModel.appendWord(ReviseWord(Word("sing", "cantar", representable = true)))
        viewModel.appendWord(ReviseWord(Word("cry", "chorar", representable = true)))

        viewModel.setWord(ReviseWord(Word("tear", "lágrima", representable = true)))

        composeTestRule.setContent {
            ImageCacheManager.init(LocalContext.current)

            for (word in viewModel.wordsToRevise.value) {
                val request = ImageRequest.Builder(LocalContext.current)
                    .data("http://10.0.2.2:8080/api/v1/image?parent=${word.tokenWord.parent}")
                    .allowHardware(true)
                    .memoryCacheKey(word.tokenWord.parent)

                ImageCacheManager.enqueue(request)
            }

            CoroutineScope(Dispatchers.Main).launch {
                ImageCacheManager.executeAllInQueue()
            }

            ProvidePreferenceLocals { Choose() }
        }
    }

    @Test
    fun renders() {
        setupScreen()

        composeTestRule.onNodeWithTag("root")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("icon-item")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("lágrima").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithText("chuva").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithText("cantar").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithText("chorar").assertExists().assertIsDisplayed()
    }

    @Test
    fun chooseRight() {
        setupScreen()

        composeTestRule.onNodeWithText("lágrima").performClick()

        composeTestRule.waitUntil(10_000) { viewModel.currentScreen.value == LessonNav.Presenter.route }
    }

    @Test
    fun chooseWrong() {
        setupScreen()

        composeTestRule.onNodeWithText("chorar").performClick()

        composeTestRule.waitUntil(10_000) { viewModel.currentScreen.value == LessonNav.Presenter.route }
    }
}