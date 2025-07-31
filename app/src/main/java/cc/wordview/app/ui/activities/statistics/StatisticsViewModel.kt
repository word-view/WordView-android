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

package cc.wordview.app.ui.activities.statistics

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import cc.wordview.app.ui.dtos.PlayerToLessonCommunicator
import cc.wordview.app.ui.activities.lesson.viewmodel.ReviseWord
import cc.wordview.app.ui.dtos.LessonToStatisticsCommunicator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _wordsLearnedAmount = MutableStateFlow(0)
    private val _accuracyPercentage = MutableStateFlow(0)
    private val _wordsPracticedAmount = MutableStateFlow(0)

    private val _words = MutableStateFlow(arrayListOf<ReviseWord>())


    val wordsLearnedAmount = _wordsLearnedAmount.asStateFlow()
    val accuracyPercentage = _accuracyPercentage.asStateFlow()
    val wordsPracticedAmount = _wordsPracticedAmount.asStateFlow()

    val words = _words.asStateFlow()

    private var tts: TextToSpeech? = null

    fun load() {
        tts = PlayerToLessonCommunicator.tts
        _words.value = PlayerToLessonCommunicator.wordsToRevise.value.distinctBy { it.tokenWord.word } as ArrayList<ReviseWord>
        _wordsLearnedAmount.update { LessonToStatisticsCommunicator.wordsLearnedAmount }
        _wordsPracticedAmount.value = PlayerToLessonCommunicator.wordsToRevise.value.size
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