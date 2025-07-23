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

package cc.wordview.app.ui.activities.lesson.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import cc.wordview.app.R
import cc.wordview.app.api.entity.Translation
import cc.wordview.app.api.getStoredJwt
import cc.wordview.app.misc.PlayerToLessonCommunicator
import cc.wordview.app.ui.activities.lesson.LessonNav
import cc.wordview.gengolex.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val translationsRepository: TranslationsRepository,
    private val saveKnownWordsRepository: SaveKnownWordsRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _currentWord = MutableStateFlow(ReviseWord())
    private val _currentScreen = MutableStateFlow("")
    private val _wordsToRevise = MutableStateFlow<ArrayList<ReviseWord>>(arrayListOf())
    private val _answerStatus = MutableStateFlow(Answer.NONE)
    private val _timer = MutableStateFlow("")
    private val _timerFinished = MutableStateFlow(false)
    private val _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    private val _knownWords = MutableStateFlow(ArrayList<String>())
    private val _translations = MutableStateFlow(ArrayList<Translation>())

    private var tts: TextToSpeech? = null

    val currentWord = _currentWord.asStateFlow()
    val currentScreen = _currentScreen.asStateFlow()
    val answerStatus = _answerStatus.asStateFlow()
    val wordsToRevise = _wordsToRevise.asStateFlow()
    val timer = _timer.asStateFlow()
    val timerFinished = _timerFinished.asStateFlow()
    val translations = _translations.asStateFlow()

    fun load() {
        tts = PlayerToLessonCommunicator.tts

        for (word in PlayerToLessonCommunicator.wordsToRevise.value)
            appendWord(word)
    }

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

        Timber.d("Word '${currentWord.value.tokenWord.word}' has no phrase")

        if (!currentWord.value.tokenWord.representable) {
            Timber.d("Word '${currentWord.value.tokenWord.word}' is not representable (skipping)")
            nextWord(answer)
        } else {
            if (!currentWord.value.isKnown) {
                setScreen(LessonNav.MeaningPresenter.route)
            } else setScreen(LessonNav.getRandomScreen().route)
        }
    }

    fun postPresent() {
        currentWord.value.isKnown = true
        _knownWords.value.add(currentWord.value.tokenWord.parent)

        if (!currentWord.value.tokenWord.representable) {
            Timber.d("Word '${currentWord.value.tokenWord.word}' is not representable (skipping)")
            nextWord()
        } else {
            setScreen(LessonNav.getRandomScreen().route)
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

    fun finishTimer(language: Language) {
        saveKnownWords(language)

        _timerFinished.update { true }
    }

    fun getTranslations() {
        val words = arrayListOf<String>()

        for (word in wordsToRevise.value) words.add(word.tokenWord.parent)

        val userLocale = appContext.resources.configuration.locales[0]
        val language = runCatching { Language.byLocaleLanguage(userLocale) }.getOrDefault(Language.ENGLISH)

        translationsRepository.apply {
            onSucceed = { translations ->
                _translations.update { translations as ArrayList<Translation> }
            }

            onFail = { _: String, _: Int ->
                Timber.e("Translations request failed")
            }

            getTranslations(language.tag, words)
        }
    }

    private fun saveKnownWords(language: Language) {
        val jwt = getStoredJwt(appContext) ?: return

        val words = arrayListOf<String>()
        for (word in _knownWords.value) words.add(word)

        saveKnownWordsRepository.apply {
            onSucceed = {
                Timber.i("Know words have been successfully saved: $it")
            }

            onFail = { message, status ->
                Timber.e("Failed to post known words \n\tmessage=$message, status=$status")
            }

            saveKnownWords(language.tag, words, jwt)
        }
    }

    fun cleanWords() {
        _wordsToRevise.update { arrayListOf() }
    }

    fun playEffect(resId: Int) {
        _mediaPlayer.value = MediaPlayer.create(appContext, resId)
        _mediaPlayer.value?.seekTo(0)
        _mediaPlayer.value?.start()
    }

    fun playEffect(answerStatus: Answer) {
        if (answerStatus == Answer.CORRECT) {
            playEffect(R.raw.correct)
        } else if (answerStatus == Answer.WRONG) {
            playEffect(R.raw.wrong)
        }
    }

    fun ttsSpeak(word: String, locale: Locale) {
        Timber.v("ttsSpeak: word=$word, locale=$locale")

        tts?.let { tts ->
            tts.language = locale
            tts.setSpeechRate(1.0f)
            tts.speak(
                word,
                TextToSpeech.QUEUE_ADD,
                null,
                null
            )
        }
    }
}