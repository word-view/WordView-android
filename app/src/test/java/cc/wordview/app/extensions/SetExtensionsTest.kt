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

import cc.wordview.app.components.extensions.without
import org.junit.Test
import org.junit.jupiter.api.Assertions

class SetExtensionsTest {
    @Test
    fun without_removesElement_whenPresent() {
        val original = setOf(1, 2, 3)
        val result = original.without(2)
        Assertions.assertEquals(setOf(1, 3), result)
    }

    @Test
    fun without_returnsSameSet_whenValueAbsent() {
        val original = setOf(1, 2, 3)
        val result = original.without(4)
        Assertions.assertEquals(original, result)
    }

    @Test
    fun without_removesOnlySpecifiedElement() {
        val original = setOf(1, 2, 3, 4)
        val result = original.without(3)
        Assertions.assertEquals(setOf(1, 2, 4), result)
    }

    @Test
    fun without_onEmptySet_returnsEmptySet() {
        val original = emptySet<Int>()
        val result = original.without(1)
        Assertions.assertTrue(result.isEmpty())
    }

    @Test
    fun without_removesStringElement() {
        val original = setOf("a", "b", "c")
        val result = original.without("b")
        Assertions.assertEquals(setOf("a", "c"), result)
    }

    @Test
    fun without_removesNullElement() {
        val original = setOf<String?>(null, "x", "y")
        val result = original.without(null)
        Assertions.assertEquals(setOf("x", "y"), result)
    }

    @Test
    fun without_returnsSameSet_whenNullNotPresent() {
        val original = setOf("x", "y")
        val result = original.without(null)
        Assertions.assertEquals(original, result)
    }

    @Test
    fun without_removesElement_whenSetHasSingleElement() {
        val original = setOf(42)
        val result = original.without(42)
        Assertions.assertTrue(result.isEmpty())
    }
}