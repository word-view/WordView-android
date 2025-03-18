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

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.app.ui.components.TextCue
import cc.wordview.app.ui.theme.WordViewTheme
import cc.wordview.gengolex.word.Word
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Rule
import org.junit.Test

class TextCueTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setup(text: String = "") {
        composeTestRule.setContent {
            WordViewTheme {
                ProvidePreferenceLocals {
                    TextCue(cue = WordViewCue(text = text))
                }
            }
        }
    }

    private fun setup(cue: WordViewCue) {
        composeTestRule.setContent {
            ProvidePreferenceLocals {
                TextCue(cue)
            }
        }
    }

    @Test
    fun renders() {
        setup("abcdefghijklmnopqrstuvwxyz 結局きたよ。")
        for (char in "abcdefghijklmnopqrstuvwxyz 結局きたよ。") {
            composeTestRule.onNodeWithText(char.toString()).assertExists()
        }
    }

    @Test
    fun dictionaryMultipleWordsEnglish() {
        val words = arrayListOf(
            Word("world", "world"),
            Word("programmed", "programmed"),
            Word("work", "work"),
            Word("feel", "feel"),
        )

        setup(
            WordViewCue(
                "Hello, world programmed to work and not to feel",
                0,
                0,
                words
            )
        )

        for (word in words) {
            composeTestRule.onNodeWithText(word.word).assertExists()
        }
    }

    @Test
    fun dictionaryMultipleWordsJapanese() {
        val words = arrayListOf(
            Word("hajimari", "始まり"),
            Word("rain", "雨"),
            Word("aruitanda", "歩いたんだ"),
            Word("kumori", "曇り"),
        )

        setup(
            WordViewCue(
                "始まりはそうだ 曇りのち雨 歩いたんだ、ここ",
                0,
                0,
                words,
            )
        )

        for (word in words) {
            composeTestRule.onNodeWithText(word.word).assertExists()
        }
    }
}