package cc.wordview.app.components.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import cc.wordview.app.components.extensions.alpha
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A composable function that displays a box with content that fades out after a specified duration.
 *
 * The [FadeOutBox] composable creates a [Box] containing the provided [content], which fades out using an
 * animation controlled by [animateFloatAsState]. The fade-out animation starts after an initial delay and
 * can be toggled by clicking the box. The animation duration and stagnation time before fading are customizable.
 * The box can be disabled to prevent automatic fading.
 *
 * @param modifier The [Modifier] to be applied to the box for layout customization. Defaults to an empty [Modifier].
 * @param duration The duration of the fade-out animation in milliseconds.
 * @param stagnationTime The time in milliseconds to wait before starting the fade-out animation after becoming visible.
 * @param disabled Whether the automatic fade-out is disabled. Defaults to false.
 * @param content A composable function that defines the content to be displayed within the box.
 */
@Composable
fun FadeOutBox(
    modifier: Modifier = Modifier,
    duration: Int,
    stagnationTime: Int,
    disabled: Boolean = false,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val fade by animateFloatAsState(
        if (visible) 1f else 0f,
        tween(duration),
        label = "FadeOutBoxAnimation",
        finishedListener = {
            if (visible) {
                coroutineScope.launch {
                    delay(stagnationTime.toLong())
                    if (!disabled) visible = false
                }
            }
        })

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            delay(500)
            if (!disabled) visible = false
        }
    }

    Box(modifier = modifier
        .alpha(fade)
        .testTag("fade-box")
        .semantics { alpha = fade }
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }) { visible = !visible }) {
        if (fade > 0.01f) {
            content()
        }
    }
}