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
 * @param contentLeft A composable function that defines the content to be displayed on the left side of the top bar.
 * @param contentRight A composable function that defines the content to be displayed on the right side of the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTopBar(modifier: Modifier = Modifier, contentLeft: @Composable () -> Unit, contentRight: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TopAppBarDefaults.TopAppBarExpandedHeight),
    ) {
        Row(
            modifier = Modifier.fillMaxHeight().align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            contentLeft()
        }
        Row(
            modifier = Modifier.fillMaxHeight().align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            contentRight()
        }
    }
}