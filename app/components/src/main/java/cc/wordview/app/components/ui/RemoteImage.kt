package cc.wordview.app.components.ui

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
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
        modifier = modifier.testTag("remote-image"),
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