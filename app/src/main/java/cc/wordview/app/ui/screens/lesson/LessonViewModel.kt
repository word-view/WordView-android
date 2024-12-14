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

package cc.wordview.app.ui.screens.lesson

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import cc.wordview.app.ui.screens.lesson.components.Answer
import cc.wordview.app.ui.screens.lesson.components.ReviseScreen
import cc.wordview.app.ui.screens.lesson.components.ReviseWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.Locale

object LessonViewModel : ViewModel() {
    private val _currentWord = MutableStateFlow(ReviseWord())
    private val _currentScreen = MutableStateFlow("")
    private val _wordsToRevise = MutableStateFlow<ArrayList<ReviseWord>>(arrayListOf())
    private val _answerStatus = MutableStateFlow(Answer.NONE)
    private val _timer = MutableStateFlow("")
    private val _timerFinished = MutableStateFlow(false)

    private var tts: TextToSpeech? = null

    val currentWord = _currentWord.asStateFlow()
    val currentScreen = _currentScreen.asStateFlow()
    val answerStatus = _answerStatus.asStateFlow()
    val wordsToRevise = _wordsToRevise.asStateFlow()
    val timer = _timer.asStateFlow()
    val timerFinished = _timerFinished.asStateFlow()

    fun nextWord(answer: Answer = Answer.NONE) {
        _wordsToRevise.update { value ->
            value.filter {
                it.tokenWord.word != currentWord.value.tokenWord.word
            } as ArrayList<ReviseWord>
        }

        if (currentWord.value.tokenWord.word != "") {
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
            Timber.d("Word '${currentWord.value.tokenWord.word}' has no phrase")

            if (!currentWord.value.tokenWord.representable) {
                Timber.d("Word '${currentWord.value.tokenWord.word}' is not representable (skipping)")
                nextWord(answer)
            } else setScreen(ReviseScreen.getRandomScreen(ReviseScreen.Translate).route)
        }
    }

    fun appendWord(reviseWord: ReviseWord) {
        if (_wordsToRevise.value.contains(reviseWord)) return

        Timber.d("Appending '${reviseWord.tokenWord.word}' to be revised")
        _wordsToRevise.update { (it + reviseWord) as ArrayList<ReviseWord> }
    }

    fun setAnswer(answer: Answer) {
        _answerStatus.update { answer }
    }

    fun setScreen(screen: String) {
        _currentScreen.update { screen }
    }

    fun setWord(word: ReviseWord) {
        _currentWord.update {
            Timber.v("setWord: previous=${it.tokenWord.word} new=${word.tokenWord.word}")
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

    fun ttsSpeak(context: Context, word: String, locale: Locale) {
        Timber.v("ttsSpeak: word=$word, locale=$locale")

        if (tts == null) {
            tts = TextToSpeech(context) {
                Timber.v("ttsSpeak: ttsStatus=$it")

                if (it == TextToSpeech.SUCCESS) {
                    tts?.let { textToSpeech ->
                        textToSpeech.language = locale
                        textToSpeech.setSpeechRate(1.0f)
                        textToSpeech.speak(
                            word,
                            TextToSpeech.QUEUE_ADD,
                            null,
                            null
                        )
                    }
                }
            }
        } else {
            tts?.let { textToSpeech ->
                textToSpeech.language = locale
                textToSpeech.setSpeechRate(1.0f)
                textToSpeech.speak(
                    word,
                    TextToSpeech.QUEUE_ADD,
                    null,
                    null
                )
            }
        }
    }
}