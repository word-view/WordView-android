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

package cc.wordview.app

import androidx.compose.ui.unit.sp
import cc.wordview.app.ui.components.calculateFontSize
import cc.wordview.app.ui.components.getFontSize
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FontSizeUtilsTest {
    @Test
    fun sizeCorrectEnglish_ShortText() {
        val size = getFontSize("Hello", "en")
        assertEquals(42.333332.sp, size)
    }

    @Test
    fun sizeCorrectEnglish_MediumText() {
        val size = getFontSize("This is a medium-length text", "en")
        assertEquals(34.666668.sp, size)
    }

    @Test
    fun sizeCorrectEnglish_LongText() {
        val size = getFontSize("This is a very long text that should have a smaller font size", "en")
        assertEquals(24.sp, size)
    }

    @Test
    fun sizeCorrectEnglish_ExceedsMaxCueSize() {
        val size = getFontSize("This text is extremely long and will exceed the max cue size set", "en")
        assertEquals(24.sp, size)
    }

    @Test
    fun sizeCorrectJapanese_ShortText() {
        val size = getFontSize("こんにちは", "ja")
        assertEquals(41.5.sp, size)
    }

    @Test
    fun sizeCorrectJapanese_MediumText() {
        val size = getFontSize("これは中くらいの長さのテキストです", "ja")
        assertEquals(35.5.sp, size)
    }

    @Test
    fun sizeCorrectJapanese_LongText() {
        val size = getFontSize("これは非常に長いテキストで、より小さなフォントサイズになるべきです", "ja")
        assertEquals(32.sp, size)
    }

    @Test
    fun calculateFontSizeWithinBounds() {
        val size = calculateFontSize(cueSize = 15, maxSize = 44.sp, minSize = 24.sp, maxCueSize = 60)
        assertEquals(39.sp, size)
    }

    @Test
    fun calculateFontSizeAtMinBoundary() {
        val size = calculateFontSize(cueSize = 61, maxSize = 44.sp, minSize = 24.sp, maxCueSize = 60)
        assertEquals(24.sp, size)
    }

    @Test
    fun calculateFontSizeAtMaxBoundary() {
        val size = calculateFontSize(cueSize = 0, maxSize = 44.sp, minSize = 24.sp, maxCueSize = 60)
        assertEquals(44.sp, size)
    }
}