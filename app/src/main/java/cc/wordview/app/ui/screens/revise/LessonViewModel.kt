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

package cc.wordview.app.ui.screens.revise

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import cc.wordview.app.ui.screens.revise.components.Answer
import cc.wordview.app.ui.screens.revise.components.ReviseScreen
import cc.wordview.app.ui.screens.revise.components.ReviseWord
import cc.wordview.gengolex.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

object LessonViewModel : ViewModel() {
    private val TAG = this::class.java.simpleName

    private val _currentWord = MutableStateFlow(ReviseWord())
    private val _currentScreen = MutableStateFlow("")
    private val _wordsToRevise = MutableStateFlow<ArrayList<ReviseWord>>(arrayListOf())
    private val _answerStatus = MutableStateFlow(Answer.NONE)
    private val _timer = MutableStateFlow("")
    private val _timerFinished = MutableStateFlow(false)

    val currentWord = _currentWord.asStateFlow()
    val currentScreen = _currentScreen.asStateFlow()
    val answerStatus = _answerStatus.asStateFlow()
    val wordsToRevise = _wordsToRevise.asStateFlow()
    val timer = _timer.asStateFlow()
    val timerFinished = _timerFinished.asStateFlow()

    fun nextWord(answer: Answer = Answer.NONE) {
        _wordsToRevise.update { value ->
            value.filter { w ->
                w.word.word != currentWord.value.word.word
            } as ArrayList<ReviseWord>
        }

        if (currentWord.value.word.word != "") {
            when (answer) {
                Answer.CORRECT -> _wordsToRevise.value.add(
                    _wordsToRevise.value.lastIndex,
                    currentWord.value
                )

                Answer.WRONG -> _wordsToRevise.value.add(
                    _wordsToRevise.value.lastIndex / 2,
                    currentWord.value
                )

                Answer.NONE -> {}
            }
        }

        setWord(_wordsToRevise.value.first())

        if (currentWord.value.hasPhrase) {
            setScreen(ReviseScreen.getRandomScreen().route)
        } else {
            Log.i(TAG, "Word '${currentWord.value.word.word}' has no phrase")
            setScreen(ReviseScreen.getRandomScreen(ReviseScreen.Translate).route)
        }
    }

    fun appendWord(word: ReviseWord) {
        for (wordd in _wordsToRevise.value) {
            if (wordd.word.word == word.word.word) return
        }

        Log.i(TAG, "Appending the word '${word.word.word}' to be revised")
        _wordsToRevise.update { old -> (old + word) as ArrayList<ReviseWord> }
    }

    fun setAnswer(answer: Answer) {
        _answerStatus.update { answer }
    }

    fun setScreen(screen: String) {
        _currentScreen.update { screen }
    }

    fun setWord(word: ReviseWord) {
        _currentWord.update {
            Log.d(TAG, "setWord: previous=${it.word.word} new=${word.word.word}")
            word
        }
    }

    fun setFormattedTime(time: String) {
        _timer.update { time }
    }

    fun finishTimer() {
        _timerFinished.update { true }
    }

    fun cleanWords() {
        _wordsToRevise.update { arrayListOf() }
    }

    fun ttsSpeak(context: Context, word: String, locale: Locale) {}
}