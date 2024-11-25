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

package cc.wordview.app.ui.screens.player

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.wordview.app.audio.AudioPlayerListener
import cc.wordview.app.audio.AudioPlayer
import cc.wordview.app.extensions.getOrDefault
import cc.wordview.app.extractor.VideoStreamInterface
import cc.wordview.app.subtitle.Lyrics
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.app.ui.components.GlobalImageLoader
import cc.wordview.app.ui.screens.lesson.LessonViewModel
import cc.wordview.app.ui.screens.lesson.components.ReviseWord
import cc.wordview.app.ui.screens.lesson.model.Phrase
import cc.wordview.app.ui.screens.lesson.model.TranslateRepository
import cc.wordview.app.ui.screens.lesson.model.phraseList
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.Parser
import coil.request.ImageRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.compose.preference.Preferences
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val playerRepository: PlayerRepository,
    private val translateRepository: TranslateRepository,
) : ViewModel() {
    private val TAG = this::class.java.simpleName

    private val _cues = MutableStateFlow(ArrayList<WordViewCue>())
    private val _playIcon = MutableStateFlow(Icons.Filled.PlayArrow)
    private val _lyrics = MutableStateFlow(Lyrics())
    private val _parser = MutableStateFlow(Parser(Language.ENGLISH))
    private val _player = MutableStateFlow(AudioPlayer())
    private val _currentCue = MutableStateFlow(WordViewCue())
    private val _playerStatus = MutableStateFlow(PlayerStatus.LOADING)
    private val _finalized = MutableStateFlow(false)
    private val _isBuffering = MutableStateFlow(false)
    private val _notEnoughWords = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow("")

    // Seekbar states
    private val _currentPosition = MutableStateFlow(0L)
    private val _bufferedPercentage = MutableStateFlow(0)

    val currentPosition = _currentPosition.asStateFlow()
    val bufferedPercentage = _bufferedPercentage.asStateFlow()

    val playIcon = _playIcon.asStateFlow()
    val player = _player.asStateFlow()
    val currentCue = _currentCue.asStateFlow()
    val playerStatus = _playerStatus.asStateFlow()
    val finalized = _finalized.asStateFlow()
    val isBuffering = _isBuffering.asStateFlow()
    val notEnoughWords = _notEnoughWords.asStateFlow()
    val errorMessage = _errorMessage.asStateFlow()

    // tracks the steps to consider that the player is
    // prepared to start playing (audio ready, lyrics ready, dictionary ready)
    private val stepsReady = MutableStateFlow(0)

    private fun computeAndCheckReadyness() {
        stepsReady.update { it + 1 }
        if (stepsReady.value == 3)
            setPlayerStatus(PlayerStatus.READY)
    }

    fun getLyrics(
        preferences: Preferences,
        context: Context,
        id: String,
        lang: Language,
        video: VideoStreamInterface
    ) {
        viewModelScope.launch {
            GlobalImageLoader.init(context)
            playerRepository.init(context)

            playerRepository.endpoint = preferences.getOrDefault("api_endpoint")
            playerRepository.onGetLyricsFail = { mesg ->
                Log.e(TAG, mesg)
                _errorMessage.update { mesg }
                setPlayerStatus(PlayerStatus.ERROR)
            }
            playerRepository.onGetLyricsSuccess = {
                val jsonObject = JsonParser.parseString(it).asJsonObject

                val lyricks = jsonObject.get("lyrics").asString
                val dictionary = jsonObject.getAsJsonArray("dictionary").toString()

                lyricsParse(preferences.getOrDefault("filter_romanizations"), lyricks)
                setCues(_lyrics.value)

                computeAndCheckReadyness()

                initParser(lang)
                addDictionary(lang.dictionaryName, dictionary)

                for (cue in _lyrics.value) {
                    val wordsFound = _parser.value.findWords(cue.text)

                    for (word in wordsFound) {
                        preloadImage(word.parent, playerRepository.endpoint, context)
                        preloadPhrases(
                            context,
                            preferences,
                            "en",
                            preferences.getOrDefault("language"),
                            word.word
                        )
                        cue.words.add(word)
                    }
                }

                computeAndCheckReadyness()
            }

            playerRepository.getLyrics(id, lang.tag, video)
        }
    }

    private fun preloadPhrases(
        context: Context,
        preferences: Preferences,
        phraseLang: String,
        wordsLang: String,
        keyword: String
    ) {
        viewModelScope.launch {
            translateRepository.init(context)
            translateRepository.endpoint = preferences.getOrDefault("api_endpoint")
            translateRepository.onGetPhraseSuccess = {
                val phrase = Gson().fromJson(it, Phrase::class.java)

                if (!phraseList.contains(phrase)) {
                    Log.i(TAG, "Preloading phrase '${phrase.phrase}'")
                    phraseList.add(phrase)
                }
            }
            translateRepository.getPhrase(phraseLang, wordsLang, keyword)
        }
    }

    private fun preloadImage(parent: String, endpoint: String, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val request = ImageRequest.Builder(context)
                    .data("$endpoint/api/v1/image?parent=$parent")
                    .allowHardware(true)
                    .memoryCacheKey(parent)
                    .build()

                GlobalImageLoader.enqueue(request)
            }
        }
    }

    fun initAudio(videoStreamUrl: String, context: Context) {
        viewModelScope.launch {
            val listener = AudioPlayerListener()

            listener.apply {
                onBuffering = { _isBuffering.update { true } }
                onReady = { _isBuffering.update { false } }

                onTogglePlay = {
                    if (it) playIconPlay()
                    else playIconPause()
                }

                onPlaybackEnd = {
                    player.value.stop()

                    for (cue in _cues.value) {
                        for (word in cue.words) {
                            val reviseWord = ReviseWord(word)

                            for (phrase in phraseList) {
                                if (phrase.words.contains(word.word)) {
                                    reviseWord.hasPhrase = true
                                }
                            }

                            LessonViewModel.appendWord(reviseWord)
                        }
                    }

                    if (LessonViewModel.wordsToRevise.value.isEmpty() || LessonViewModel.wordsToRevise.value.size < 3) {
                        _notEnoughWords.update { true }
                    } else _finalized.update { true }
                }
            }

            player.value.apply {
                onPositionChange = { pos, bufferedPercentage ->
                    setCurrentCue(_lyrics.value.getCueAt(pos))
                    _currentPosition.update { pos.toLong() }
                    _bufferedPercentage.update { bufferedPercentage }
                }
                onInitializeFail = { setPlayerStatus(PlayerStatus.ERROR) }
                onPrepared = { computeAndCheckReadyness() }

                initialize(videoStreamUrl, context, listener)
            }
        }
    }

    private fun setCues(cues: ArrayList<WordViewCue>) {
        _cues.update { cues }
    }

    private fun playIconPause() {
        _playIcon.update { Icons.Filled.PlayArrow }
    }

    private fun playIconPlay() {
        _playIcon.update { Icons.Filled.Pause }
    }

    private fun lyricsParse(filterRomanizations: Boolean, res: String) {
        val newLyrics = Lyrics()
        newLyrics.parse(filterRomanizations, res)
        _lyrics.update { newLyrics }
    }

    private fun initParser(language: Language) {
        _parser.update { Parser(language) }
    }

    private fun addDictionary(name: String, dictionary: String) {
        _parser.value.addDictionary(name, dictionary)
    }

    private fun setCurrentCue(cue: WordViewCue) {
        _currentCue.update { cue }
    }

    private fun setPlayerStatus(playerStatus: PlayerStatus) {
        _playerStatus.update { playerStatus }
    }
}