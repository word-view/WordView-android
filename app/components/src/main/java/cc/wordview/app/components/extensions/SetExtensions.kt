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

/**
 * Returns a new [Set] containing all the elements of this set except the specified [value].
 *
 * If [value] is not present in the set, the original set is returned unchanged.
 *
 * @param value The element to be removed from the set.
 * @return A new set without the specified [value], or the original set if [value] is not present.
 */
fun <T> Set<T>.without(value: T): Set<T> {
    if (!this.contains(value)) return this
    return this.filterNot { it == value }.toSet()
}