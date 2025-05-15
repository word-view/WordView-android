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

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import cc.wordview.app.ComposeTest
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.app.ui.components.TextCue
import cc.wordview.app.ui.theme.WordViewTheme
import cc.wordview.gengolex.word.Word
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Test

class TextCueTest : ComposeTest() {
    private fun setup(text: String = "", cue: WordViewCue? = null) {
        composeTestRule.setContent {
            WordViewTheme {
                ProvidePreferenceLocals {
                    TextCue(cue = cue ?: WordViewCue(text = text))
                }
            }
        }
    }

    @Test
    fun renders() {
        setup("abcdefghijklmnopqrstuvwxyz 結局きたよ。")
        for (char in "abcdefghijklmnopqrstuvwxyz 結局きたよ。") {
            composeTestRule.onNodeWithText(char.toString()).assertExists()
        }

        composeTestRule.onNodeWithTag("text-cue-plain")
            .assertDoesNotExist()
    }

    @Test
    fun rendersWord() {
        val cue = WordViewCue(
            text = "abcdefghijklmnopqrstuvwxyz 結局きたよ。",
            words = arrayListOf(Word("test", "結局きたよ。"))
        )

        setup(cue.text, cue)

        for (char in "abcdefghijklmnopqrstuvwxyz 結局きたよ。") {
            composeTestRule.onNodeWithText(char.toString()).assertExists()
        }

        composeTestRule.onAllNodesWithTag("text-cue-plain")
            .assertCountEquals(6)
    }

    @Test
    fun ignoresWordWithoutParent() {
        val cue = WordViewCue(
            text = "abcdefghijklmnopqrstuvwxyz 結局きたよ。",
            words = arrayListOf(Word("", "結局きたよ。"))
        )

        setup(cue.text, cue)

        for (char in "abcdefghijklmnopqrstuvwxyz 結局きたよ。") {
            composeTestRule.onNodeWithText(char.toString()).assertExists()
        }

        composeTestRule.onNodeWithTag("text-cue-plain")
            .assertDoesNotExist()
    }
}