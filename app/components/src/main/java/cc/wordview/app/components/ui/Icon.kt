package cc.wordview.app.components.ui

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag

/**
 * Simplification of [androidx.compose.material3.Icon] to automatically
 * set `contentDescription` to `null`
 *
 * @param imageVector ImageVector to draw inside this icon
 */
@Composable
fun Icon(imageVector: ImageVector, modifier: Modifier = Modifier) {
    Icon(imageVector, contentDescription = null, modifier = modifier.testTag("contentDescriptionless-icon"))
}