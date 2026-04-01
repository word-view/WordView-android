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
 * Returns an array containing the specified amount of elements, sorted randomly.
 *
 * @param amount The number of elements to return.
 * @return A list of randomly selected elements from the original list.
 * @throws IllegalArgumentException if amount is negative.
 */
fun <T> Collection<T>.random(amount: Int): List<T> {
    require(amount >= 0) { "Amount must be non-negative." }
    return this.shuffled().take(amount)
}