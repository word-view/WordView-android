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

package cc.wordview.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag

/**
 * Simplification of `androidx.compose.material3.Icon` to automatically
 * set `contentDescription` to `null`
 *
 * @param imageVector ImageVector to draw inside this icon
 */
@Composable
fun Icon(imageVector: ImageVector) {
    androidx.compose.material3.Icon(imageVector, contentDescription = null, modifier = Modifier.testTag("contentDescriptionless-icon"))
}