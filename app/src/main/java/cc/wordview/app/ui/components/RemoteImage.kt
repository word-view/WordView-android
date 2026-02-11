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

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import cc.wordview.app.components.ui.AsyncImagePlaceholders
import coil3.compose.AsyncImage
import me.vponomarenko.compose.shimmer.shimmer

/**
 * Displays a remote image using Coil's [AsyncImage] while providing a simple, unified way
 * to apply shapes, modifiers and such.
 *
 * @param modifier Optional [Modifier] applied to the outer [Surface].
 * @param shape The shape to apply to the entire image container. Defaults to [RectangleShape].
 * @param model The image model for Coil (URL, file, resource, etc.).
 * @param contentDescriptor Text used for accessibility services.
 * @param asyncImagePlaceholders Container of placeholder resources for error states.
 */
@Composable
fun RemoteImage(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    model: Any? = null,
    contentDescriptor: String? = null,
    asyncImagePlaceholders: AsyncImagePlaceholders,
    contentScale: ContentScale = ContentScale.FillHeight
) {
    Surface(
        modifier = modifier,
        shape = shape
    ) {
        Box(
            Modifier
                .zIndex(-1f)
                .shimmer()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        )
        AsyncImage(
            model = model,
            placeholder = null,
            error = painterResource(id = if (isSystemInDarkTheme()) asyncImagePlaceholders.noConnectionWhite else asyncImagePlaceholders.noConnectionDark),
            contentDescription = contentDescriptor,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
        )
    }
}