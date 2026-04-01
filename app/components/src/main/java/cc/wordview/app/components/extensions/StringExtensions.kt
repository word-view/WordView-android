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

import java.net.URLEncoder

/**
 * Returns a URL-encoded version of this [String].
 *
 * Encodes the string using the platform's default charset so it can be safely included in a URL.
 * If encoding fails, returns the original string.
 *
 * @receiver The string to encode.
 * @return The URL-encoded string, or the original string if encoding is not possible.
 */
fun String.asURLEncoded(): String {
    @Suppress("DEPRECATION")
    return URLEncoder.encode(this) ?: this
}

/**
 * Returns a copy of this string with the first character in uppercase and the rest in lowercase.
 *
 * If the string is empty, this will throw an [IndexOutOfBoundsException].
 *
 * @receiver The string to capitalize.
 * @return The capitalized string.
 */
fun String.capitalize(): String {
    return this[0].uppercase() + this.lowercase().substring(1)
}

