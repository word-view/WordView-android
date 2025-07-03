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
import cc.wordview.app.BuildConfig
import cc.wordview.app.api.APIUrl
import cc.wordview.app.api.getStoredJwt
import cc.wordview.app.api.request.AuthenticatedStringRequest
import cc.wordview.app.ui.activities.lesson.LessonNav
import cc.wordview.gengolex.Language
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.util.Locale

object LessonViewModel : ViewModel() {
    private val _currentWord = MutableStateFlow(ReviseWord())
    private val _currentScreen = MutableStateFlow("")
    private val _wordsToRevise = MutableStateFlow<ArrayList<ReviseWord>>(arrayListOf())
    private val _answerStatus = MutableStateFlow(Answer.NONE)
    private val _timer = MutableStateFlow("")
    private val _timerFinished = MutableStateFlow(false)
    private val _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    private val _knownWords = MutableStateFlow(ArrayList<String>())

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

    fun finishTimer(context: Context? = null, language: Language) {
        if (context == null)
            Timber.w("Words learned in this session won't be saved since context was not specified to finishTimer")

        context?.let { saveKnownWords(it, language) }

        _timerFinished.update { true }
    }

    private fun saveKnownWords(context: Context, language: Language) {
        // TODO: The way this request is made does not follow the same way other requests are made in the app (through repositories).
        val endpoint = BuildConfig.API_BASE_URL
        val queue = Volley.newRequestQueue(context)
        val jwt = getStoredJwt(context)!!

        val url = APIUrl("$endpoint/api/v1/lesson/words/known")

        val jsonArray = JSONArray()

        for (word in _knownWords.value)
            jsonArray.put(word)

        val json = JSONObject()
            .put("language", language.tag)
            .put("words", jsonArray)

        val request = AuthenticatedStringRequest(
            url.getURL(),
            jwt,
            Request.Method.POST,
            json,
            { Timber.i("Know words have been successfully saved: $it") },
            { message, status -> Timber.e("Failed to post known words \n\tmessage=$message, status=$status") }
        )

        queue.add(request)
    }

    fun cleanWords() {
        _wordsToRevise.update { arrayListOf() }
    }

    fun playEffect(context: Context, resId: Int) {
        _mediaPlayer.value = MediaPlayer.create(context, resId)
        _mediaPlayer.value?.seekTo(0)
        _mediaPlayer.value?.start()
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