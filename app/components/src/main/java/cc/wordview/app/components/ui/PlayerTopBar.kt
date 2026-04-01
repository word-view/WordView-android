package cc.wordview.app.components.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A composable function that displays a top bar for the WordView player with customizable content.
 *
 * @param modifier The [Modifier] to be applied to the top bar for layout customization. Defaults to an empty [Modifier].
 * @param content A composable function that defines the content to be displayed within the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTopBar(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TopAppBarDefaults.TopAppBarExpandedHeight),
        contentAlignment = Alignment.TopStart,
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}