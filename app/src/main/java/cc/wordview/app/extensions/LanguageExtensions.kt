package cc.wordview.app.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import cc.wordview.app.R
import cc.wordview.gengolex.Language

fun Language.displayName(): String {
    return name.lowercase().capitalize()
}

@Composable
fun Language.getFlag(): Painter {
    return when (this.tag) {
        "pt" -> painterResource(R.drawable.br)
        "en" -> painterResource(R.drawable.us)
        "ja" -> painterResource(R.drawable.ja)
        //
        else -> painterResource(R.drawable.us)
    }
}