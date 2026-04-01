package cc.wordview.app.components.ui

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

/**
 * A composable function that displays an image with a fade-in animation effect.
 *
 * The [FadeInAsyncImage] composable renders a [Bitmap] image using [AsyncImage] with a fade-in animation
 * when the image is displayed.
 *
 * @param image The [Bitmap] image to be displayed. Can be null, in which case no image is shown.
 */
@Composable
fun FadeInAsyncImage(image: Bitmap?) {
    var isVisible by rememberSaveable { mutableStateOf(false) }

    // This prevents the background from "flashing" due to recompositions (probably)
    val img = remember { image }

    LaunchedEffect(img) {
        isVisible = false
        isVisible = true
    }

    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 500,
                easing = EaseInExpo
            )
        )
    ) {
        AsyncImage(
            model = img,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.15f),
            contentScale = ContentScale.FillWidth
        )
    }
}