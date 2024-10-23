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

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.wordview.app.ui.screens.revise.WordReviseViewModel
import cc.wordview.app.ui.screens.revise.components.ReviseScreen
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.Preferences
import javax.inject.Inject

@HiltViewModel
class TranslateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val translateRepository: TranslateRepository
) : ViewModel() {
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

    fun getPhrase(
        preferences: Preferences,
        context: Context,
        phraseLang: String,
        wordsLang: String,
        keyword: String
    ) {
        viewModelScope.launch {
            translateRepository.init(context)
            translateRepository.endpoint = preferences["api_endpoint"] ?: "10.0.2.2"
            translateRepository.onGetPhraseFail = {
                WordReviseViewModel.setScreen(ReviseScreen.getRandomScreen(ReviseScreen.Translate).route)
                cleanup()
            }
            translateRepository.onGetPhraseSuccess = {
                val phrase = Gson().fromJson(it, Phrase::class.java)

                setPhrase(phrase.phrase)
                appendWords(phrase.words)
            }
            translateRepository.getPhrase(phraseLang, wordsLang, keyword)
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