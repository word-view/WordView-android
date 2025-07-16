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

import cc.wordview.gengolex.Language
import org.junit.Test
import org.junit.jupiter.api.Assertions

class LanguageExtensionsTest {
    @Test
    fun getDisplayName_Portuguese() {
        val lang = Language.PORTUGUESE
        Assertions.assertEquals("Portuguese", lang.displayName())
    }

    @Test
    fun getDisplayName_English() {
        val lang = Language.ENGLISH
        Assertions.assertEquals("English", lang.displayName())
    }

    @Test
    fun getDisplayName_Japanese() {
        val lang = Language.JAPANESE
        Assertions.assertEquals("Japanese", lang.displayName())
    }
}