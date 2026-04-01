package cc.wordview.app.components.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

@Immutable
class AsyncImagePlaceholders(
    @DrawableRes val noConnectionWhite: Int,
    @DrawableRes val noConnectionDark: Int,
)