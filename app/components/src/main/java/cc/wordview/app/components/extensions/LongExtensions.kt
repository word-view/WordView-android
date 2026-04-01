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
 * Calculates what percentage [num] is of this [Long] value.
 *
 * Returns 0.0 if either this value or [num] is less than or equal to zero.
 *
 * @param num The number to calculate as a percentage of this value.
 * @return The percentage that [num] is of this [Long] value, as a [Double].
 */
fun Long.percentageOf(num: Long): Double {
    if (num <= 0 || this <= 0) return 0.toDouble()
    return ((num.toDouble() / this) * 100)
}