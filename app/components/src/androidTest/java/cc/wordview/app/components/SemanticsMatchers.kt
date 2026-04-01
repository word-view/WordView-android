package cc.wordview.app.components

import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import cc.wordview.app.components.extensions.AlphaKey

fun hasAlpha(expectedAlpha: Float): SemanticsMatcher {
    return SemanticsMatcher("has alpha value of $expectedAlpha") { node ->
        node.config.getOrNull(AlphaKey) == expectedAlpha
    }
}