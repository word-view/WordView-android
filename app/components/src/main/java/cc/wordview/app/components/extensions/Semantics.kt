package cc.wordview.app.components.extensions

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver

val AlphaKey = SemanticsPropertyKey<Float>("AlphaKey")
var SemanticsPropertyReceiver.alpha by AlphaKey