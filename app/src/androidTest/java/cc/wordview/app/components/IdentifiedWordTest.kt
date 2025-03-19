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
import cc.wordview.app.ComposeTest
import cc.wordview.app.ui.components.IdentifiedWord
import cc.wordview.app.ui.theme.WordViewTheme
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.word.Specifier
import cc.wordview.gengolex.word.Syntax
import cc.wordview.gengolex.word.Time
import cc.wordview.gengolex.word.Word
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Rule
import org.junit.Test

class IdentifiedWordTest : ComposeTest() {
    private fun setup(word: Word, text: String, langtag: String) {
        composeTestRule.setContent {
            WordViewTheme {
                ProvidePreferenceLocals {
                    IdentifiedWord(
                        word = word,
                        text = text,
                        langtag = langtag
                    )
                }
            }
        }
    }

    @Test
    fun rendersBareBones() {
        setup(
            word = Word("world", "world"),
            text = "world",
            langtag = Language.ENGLISH.tag
        )

        composeTestRule.onNodeWithText("Noun").assertExists()
        for (char in "world") {
            composeTestRule.onNodeWithText(char.toString()).assertExists()
        }
    }

    @Test
    fun rendersNegativeSyntax() {
        val word = Word(
            parent = "world",
            word = "world",
            syntax = Syntax(
                default = Specifier(start = 0, end = 1),
                negative = Specifier(start = 2, end = 4),
                conditional = null
            )
        )
        setup(
            word = word,
            text = "world",
            langtag = Language.ENGLISH.tag
        )

        composeTestRule.onNodeWithText("Noun").assertExists()
        for (char in "world") {
            composeTestRule.onNodeWithText(char.toString()).assertExists()
        }
    }

    @Test
    fun rendersConditionalSyntax() {
        val word = Word(
            parent = "world",
            word = "world",
            syntax = Syntax(
                default = Specifier(start = 0, end = 1),
                conditional = Specifier(start = 2, end = 4),
                negative = null
            )
        )
        setup(
            word = word,
            text = "world",
            langtag = Language.ENGLISH.tag
        )

        composeTestRule.onNodeWithText("Noun").assertExists()
        for (char in "world") {
            composeTestRule.onNodeWithText(char.toString()).assertExists()
        }
    }

    @Test
    fun rendersPast() {
        val word = Word(
            parent = "world",
            word = "world",
            time = Time.PAST.toString()
        )
        setup(
            word = word,
            text = "world",
            langtag = Language.ENGLISH.tag
        )

        composeTestRule.onNodeWithText("Past").assertExists()
        for (char in "world") {
            composeTestRule.onNodeWithText(char.toString()).assertExists()
        }
    }

    @Test
    fun rendersPresent() {
        val word = Word(
            parent = "world",
            word = "world",
            time = Time.PRESENT.toString()
        )
        setup(
            word = word,
            text = "world",
            langtag = Language.ENGLISH.tag
        )

        composeTestRule.onNodeWithText("Present").assertExists()
        for (char in "world") {
            composeTestRule.onNodeWithText(char.toString()).assertExists()
        }
    }

    @Test
    fun rendersFuture() {
        val word = Word(
            parent = "world",
            word = "world",
            time = Time.FUTURE.toString()
        )
        setup(
            word = word,
            text = "world",
            langtag = Language.ENGLISH.tag
        )

        composeTestRule.onNodeWithText("Future").assertExists()
        for (char in "world") {
            composeTestRule.onNodeWithText(char.toString()).assertExists()
        }
    }
}