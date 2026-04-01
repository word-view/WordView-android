package cc.wordview.app.components.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Instantly animates the appearance and disappearance of its content
 *
 * @param modifier
 * @param content Content to appear in
 */
@Composable
fun InstantAnimatedVisibility(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OneTimeEffect { visible = true }
    AnimatedVisibility(visible = visible, modifier = modifier) { content() }
}