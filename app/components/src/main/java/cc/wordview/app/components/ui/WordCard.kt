package cc.wordview.app.components.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A composable function that displays a card containing text with customizable click behavior.
 *
 * @param text The text to be displayed inside the card.
 * @param onClick The callback invoked when the card is clicked.
 * @param modifier The [Modifier] to be applied to the card for layout customization. Defaults to an empty [Modifier].
 * @param enabled Whether the card is clickable. Defaults to true.
 */
@Composable
fun WordCard(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        enabled = enabled,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
            style = typography.titleLarge,
            softWrap = false
        )
    }
}