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

package cc.wordview.app.extensions

import cc.wordview.app.components.extensions.asURLEncoded
import cc.wordview.app.components.extensions.capitalize
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows

class StringExtensionsTest {
    @Test
    fun asURLEncoded_encodesSpacesAndSpecialChars() {
        val original = "hello world!"
        val encoded = original.asURLEncoded()
        // "hello world!" becomes "hello+world%21" with URLEncoder.encode default behavior
        Assertions.assertEquals("hello+world%21", encoded)
    }

    @Test
    fun asURLEncoded_emptyString() {
        val original = ""
        val encoded = original.asURLEncoded()
        Assertions.assertEquals("", encoded)
    }

    @Test
    fun asURLEncoded_alphanumeric() {
        val original = "abc123"
        val encoded = original.asURLEncoded()
        // No special characters, should be unchanged
        Assertions.assertEquals("abc123", encoded)
    }

    @Test
    fun asURLEncoded_nonAsciiCharacters() {
        val original = "café"
        val encoded = original.asURLEncoded()
        // "é" is encoded
        Assertions.assertEquals("caf%C3%A9", encoded)
    }

    @Test
    fun capitalize_simpleLowercase() {
        val original = "hello"
        val capitalized = original.capitalize()
        Assertions.assertEquals("Hello", capitalized)
    }

    @Test
    fun capitalize_allUppercase() {
        val original = "WORD"
        val capitalized = original.capitalize()
        Assertions.assertEquals("Word", capitalized)
    }

    @Test
    fun capitalize_mixedCase() {
        val original = "hELLO"
        val capitalized = original.capitalize()
        Assertions.assertEquals("Hello", capitalized)
    }

    @Test
    fun capitalize_oneLetter() {
        val original = "a"
        val capitalized = original.capitalize()
        Assertions.assertEquals("A", capitalized)
    }

    @Test
    fun capitalize_emptyString_throwsException() {
        assertThrows<IndexOutOfBoundsException> { "".capitalize() }
    }
}