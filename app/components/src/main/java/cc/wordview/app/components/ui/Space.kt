package cc.wordview.app.components.ui

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp

/**
 * Simplified version of the [androidx.compose.foundation.layout.Spacer] composable.
 *
 * @param size The size of the space
 */
@Composable
fun Space(size: Dp) {
    androidx.compose.foundation.layout.Spacer(Modifier.size(size).testTag("space"))
}