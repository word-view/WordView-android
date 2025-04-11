package cc.wordview.app.extensions

import cc.wordview.gengolex.Language

fun Language.displayName(): String {
    return name.lowercase().capitalize()
}