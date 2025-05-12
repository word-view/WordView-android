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

package cc.wordview.app.ui.activities.lesson.composables

import kotlin.random.Random

/**
 * All the modes that the `Drag` screen can be in.
 */
enum class DragMode {
    /**
     * Means that the middle object should be a Icon
     */
    ICON,

    /**
     * Means that the middle object should e a Word
     */
    WORD;

    companion object {
        fun random(): DragMode {
            val values = enumValues<DragMode>()
            return values[Random.nextInt(values.size)]
        }
    }
}