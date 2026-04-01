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

import androidx.media3.common.util.Assertions
import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class CollectionExtensionsTest {
    @Test
    fun getRandomAmount() {
        val array = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        val a1 = array.random(2)
        assertEquals(2, a1.size)

        val a2 = array.random(4)
        assertEquals(4, a2.size)

        val a3 = array.random(6)
        assertEquals(6, a3.size)
    }

    @Test
    fun getRandomNegativeValue() {
        val array = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        assertFailsWith<IllegalArgumentException>(block = { array.random(-1) })
    }
}