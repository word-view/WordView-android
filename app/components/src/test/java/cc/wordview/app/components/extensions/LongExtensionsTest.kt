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

package cc.wordview.app.components.extensions

import org.junit.Test
import kotlin.test.assertEquals

class LongExtensionsTest {
    @Test
    fun calculatePercentageOf_200_50() {
        val total = 200L
        val part = 50L

        val percent = total.percentageOf(part)

        assertEquals(25.0, percent)
    }

    @Test
    fun calculatePercentageOf_equalValues() {
        val total = 100L
        val part = 100L

        val percent = total.percentageOf(part)

        assertEquals(100.0, percent)
    }

    @Test
    fun calculatePercentageOf_partZero() {
        val total = 100L
        val part = 0L

        val percent = total.percentageOf(part)

        assertEquals(0.0, percent)
    }

    @Test
    fun calculatePercentageOf_totalZero() {
        val total = 0L
        val part = 50L

        val percent = total.percentageOf(part)

        assertEquals(0.0, percent)
    }

    @Test
    fun calculatePercentageOf_bothZero() {
        val total = 0L
        val part = 0L

        val percent = total.percentageOf(part)

        assertEquals(0.0, percent)
    }

    @Test
    fun calculatePercentageOf_negativeTotal() {
        val total = -100L
        val part = 50L

        val percent = total.percentageOf(part)

        assertEquals(0.0, percent)
    }

    @Test
    fun calculatePercentageOf_negativePart() {
        val total = 100L
        val part = -50L

        val percent = total.percentageOf(part)

        assertEquals(0.0, percent)
    }

    @Test
    fun calculatePercentageOf_bothNegative() {
        val total = -100L
        val part = -50L

        val percent = total.percentageOf(part)

        assertEquals(0.0, percent)
    }

    @Test
    fun calculatePercentageOf_partGreaterThanTotal() {
        val total = 50L
        val part = 100L

        val percent = total.percentageOf(part)

        assertEquals(200.0, percent)
    }

    @Test
    fun calculatePercentageOf_largeNumbers() {
        val total = 1_000_000_000L
        val part = 250_000_000L

        val percent = total.percentageOf(part)

        assertEquals(25.0, percent)
    }

    @Test
    fun calculatePercentageOf_smallNumbers() {
        val total = 2L
        val part = 1L

        val percent = total.percentageOf(part)

        assertEquals(50.0, percent)
    }
}