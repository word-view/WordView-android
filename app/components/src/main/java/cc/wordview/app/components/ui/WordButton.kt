package cc.wordview.app.components.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A composable function that displays a button with customizable text content.
 *
 * @param text A composable function that defines the content to be displayed inside the button.
 * @param onClick The callback invoked when the button is clicked.
 * @param modifier The [Modifier] to be applied to the button for layout customization. Defaults to an empty [Modifier].
 * @param enabled Whether the button is clickable. Defaults to true.
 */
@Composable
fun WordButton(text: @Composable () -> Unit, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Card(
        modifier = modifier.width(420.dp),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        enabled = enabled,
    ) {
        Box(Modifier.padding(vertical = 12.dp).align(Alignment.CenterHorizontally)) {
            text()
        }
    }
}