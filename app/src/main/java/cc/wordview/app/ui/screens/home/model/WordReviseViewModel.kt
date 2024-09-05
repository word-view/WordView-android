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

package cc.wordview.app.ui.screens.home.model

import androidx.lifecycle.ViewModel
import cc.wordview.app.ui.screens.home.revise.Answer
import cc.wordview.gengolex.languages.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object WordReviseViewModel : ViewModel() {
    private val _currentWord = MutableStateFlow(Word("", ""))
    private val _screen = MutableStateFlow("")
    private val _wordsToRevise = MutableStateFlow<List<Word>>(listOf())
    private val _answerStatus = MutableStateFlow(Answer.NONE)
    private val _formattedTime = MutableStateFlow("")

    val currentWord = _currentWord.asStateFlow()
    val screen = _screen.asStateFlow()
    val wordsToRevise = _wordsToRevise.asStateFlow()
    val answerStatus = _answerStatus.asStateFlow()
    val formattedTime = _formattedTime.asStateFlow()

    fun nextWord() {
        val wordsWithoutCurrent = _wordsToRevise.value.filter { w -> w.word != currentWord.value.word }
        _wordsToRevise.update { wordsWithoutCurrent }
        _currentWord.update { _wordsToRevise.value.random() }
    }

    fun setWord(word: Word) {
        _currentWord.update { word }
    }

    fun setWordsToRevise(words: List<Word>) {
        _wordsToRevise.update { words }
    }

    fun setScreen(screen: String) {
        _screen.update { screen }
    }

    fun setFormattedTime(time: String) {
        _formattedTime.update { time }
    }

    fun setAnswer(answer: Answer) {
        _answerStatus.update { answer }
    }
}