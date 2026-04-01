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

import androidx.annotation.IntRange
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.dragGestures(
    onDragStart: (offset: Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
): Modifier {
    return this.pointerInput(Unit) {
        detectDragGestures(
            onDragStart,
            onDragEnd,
            onDragCancel,
            onDrag
        )
    }
}

fun Modifier.detectTapGestures(
    onDoubleTap: ((Offset) -> Unit)? = null,
    onLongPress: ((Offset) -> Unit)? = null,
    onPress: suspend PressGestureScope.(Offset) -> Unit = { },
    onTap: ((Offset) -> Unit)? = null
): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = onDoubleTap,
            onLongPress = onLongPress,
            onPress = onPress,
            onTap = onTap
        )
    }
}

fun Modifier.fillMaxWidth(@IntRange(from = 0, to = 100) percentage: Int): Modifier {
    val fraction = (percentage / 100f)
    return this.fillMaxWidth(fraction)
}