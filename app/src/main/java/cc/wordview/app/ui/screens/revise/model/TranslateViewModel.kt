/*
 * Copyright (c) 2024 Arthur Araujo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package cc.wordview.app.ui.screens.revise.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import cc.wordview.gengolex.languages.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object TranslateViewModel : ViewModel() {
    private val _phrase = MutableStateFlow("")

    private val _answerWordPool = mutableStateListOf<Word>()
    private val _wordPool = mutableStateListOf<Word>()
    private val _originalPoolOrder = mutableStateListOf<Word>()
    private val _wrongOrderedWords = mutableStateListOf<Int>()

    val phrase = _phrase.asStateFlow()
    val answerWordPool get() = _answerWordPool
    val wordPool get() = _wordPool
    val originalPoolOrder get() = _originalPoolOrder
    val wrongOrderedWords get() = _wrongOrderedWords

    fun setPhrase(phrase: String) {
       _phrase.update { phrase }
    }

    fun appendWords(words: ArrayList<Word>) {
        wordPool.addAll(words.shuffled())
        originalPoolOrder.addAll(words)
    }

    fun addToAnswer(word: Word) {
        answerWordPool.add(word)
        wordPool.remove(word)
    }

    fun removeFromAnswer(word: Word) {
        wordPool.add(word)
        answerWordPool.remove(word)
    }

    fun cleanup() {
        _phrase.update { "" }
        _answerWordPool.removeAll(_answerWordPool)
        _wordPool.removeAll(_wordPool)
        _originalPoolOrder.removeAll(_originalPoolOrder)
        _wrongOrderedWords.removeAll(_wrongOrderedWords)
    }
}