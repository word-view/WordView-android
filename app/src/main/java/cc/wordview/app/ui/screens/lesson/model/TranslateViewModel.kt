/*
 * Copyright (c) 2025 Arthur Araujo
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

package cc.wordview.app.ui.screens.lesson.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.wordview.app.ui.screens.lesson.LessonViewModel
import cc.wordview.app.ui.screens.lesson.components.ReviseScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TranslateViewModel @Inject constructor() : ViewModel() {
    private val _phrase = MutableStateFlow("")

    private val _answerWordPool = mutableStateListOf<String>()
    private val _wordPool = mutableStateListOf<String>()
    private val _originalPoolOrder = mutableStateListOf<String>()
    private val _wrongOrderedWords = mutableStateListOf<Int>()

    val phrase = _phrase.asStateFlow()
    val answerWordPool get() = _answerWordPool
    val wordPool get() = _wordPool
    val originalPoolOrder get() = _originalPoolOrder
    val wrongOrderedWords get() = _wrongOrderedWords

    fun getPhrase(keyword: String) {
        viewModelScope.launch {
            for (phrase in phraseList) {
                if (phrase.words.contains(keyword)) {
                    setPhrase(phrase.phrase)
                    appendWords(phrase.words)
                }
            }

            if (_phrase.value == "") {
                Timber.i("No phrase found for keyword=$keyword (skipping)")
                LessonViewModel.setScreen(ReviseScreen.getRandomScreen(ReviseScreen.Translate).route)
                cleanup()
            }
        }
    }

    private fun setPhrase(phrase: String) {
        _phrase.update { phrase }
    }

    private fun appendWords(words: List<String>) {
        wordPool.addAll(words.shuffled())
        originalPoolOrder.addAll(words)
    }

    fun addToAnswer(word: String) {
        answerWordPool.add(word)
        wordPool.remove(word)
    }

    fun removeFromAnswer(word: String) {
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