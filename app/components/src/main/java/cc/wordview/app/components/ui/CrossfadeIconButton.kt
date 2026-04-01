package cc.wordview.app.components.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A composable function that displays a circular button with a crossfade-animated icon.
 *
 * The [CrossfadeIconButton] composable creates a Material 3 [Button] with a circular shape, containing an
 * [Icon] that animates changes using [Crossfade].
 *
 * @param modifier The [Modifier] to be applied to the button for layout customization. Defaults to an empty [Modifier].
 * @param colors The [ButtonColors] to style the button's container and content. Defaults to a transparent container with primary content color.
 * @param onClick The callback invoked when the button is clicked.
 * @param enabled Whether the button is clickable. Defaults to true.
 * @param icon The [ImageVector] representing the icon to display inside the button.
 * @param size The [Dp] size of the button, applied to both width and height.
 */
@Composable
fun CrossfadeIconButton(
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary
    ),
    onClick: () -> Unit,
    enabled: Boolean = true,
    icon: ImageVector,
    size: Dp
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(size),
        colors = colors,
        enabled = enabled,
        shape = CircleShape,
    ) {
        Crossfade(targetState = icon, label = "a") {
            Icon(
                imageVector = it,
                modifier = Modifier.size(60.dp),
                contentDescription = ""
            )
        }
    }
}