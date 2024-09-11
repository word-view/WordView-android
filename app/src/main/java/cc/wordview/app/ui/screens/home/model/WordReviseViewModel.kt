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

import android.util.Log
import cc.wordview.app.ui.screens.home.revise.Answer
import cc.wordview.app.ui.screens.home.revise.algo.ReviseWord
import cc.wordview.gengolex.languages.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object WordReviseViewModel : InitializeViewModel() {
    private val TAG = WordReviseViewModel::class.java.simpleName

    private val _currentWord = MutableStateFlow(ReviseWord())
    private val _screen = MutableStateFlow("")
    private val _wordsToRevise = MutableStateFlow<List<ReviseWord>>(listOf())
    private val _answerStatus = MutableStateFlow(Answer.NONE)
    private val _formattedTime = MutableStateFlow("")

    val currentWord = _currentWord.asStateFlow()
    val screen = _screen.asStateFlow()
    val wordsToRevise = _wordsToRevise.asStateFlow()
    val answerStatus = _answerStatus.asStateFlow()
    val formattedTime = _formattedTime.asStateFlow()

    fun nextWord() {
        val wordsWithoutCurrent = _wordsToRevise.value.filter { w -> w.word?.word != currentWord.value.word?.word }
        _wordsToRevise.update { wordsWithoutCurrent }
        setWord(_wordsToRevise.value.random())
    }

    fun setWord(word: ReviseWord) {
        _currentWord.update {
            Log.d(TAG, "Updating word from '${it.word}' to '${word.word}'")
            word
        }
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

    fun appendWord(word: ReviseWord) {
        Log.d(TAG, "Appending the word '${word.word.word}' to be revised")
        _wordsToRevise.update { old -> old + word }
    }
}