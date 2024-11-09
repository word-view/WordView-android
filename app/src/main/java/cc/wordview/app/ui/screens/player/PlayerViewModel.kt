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
import cc.wordview.app.ui.screens.revise.WordReviseViewModel
import cc.wordview.app.ui.screens.revise.components.ReviseWord
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.Parser
import coil.ImageLoader
import coil.request.ImageRequest
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
    private val playerRepository: PlayerRepository
) : ViewModel() {
    private val TAG = WordReviseViewModel::class.java.simpleName

    private val _cues = MutableStateFlow(ArrayList<WordViewCue>())
    private val _playIcon = MutableStateFlow(Icons.Filled.PlayArrow)
    private val _lyrics = MutableStateFlow(Lyrics())
    private val _parser = MutableStateFlow(Parser(Language.ENGLISH))
    private val _player = MutableStateFlow(AudioPlayer())
    private val _currentCue = MutableStateFlow(WordViewCue())
    private val _playerStatus = MutableStateFlow(PlayerStatus.LOADING)
    private val _finalized = MutableStateFlow(false)
    private val _isBuffering = MutableStateFlow(false)

    // Seekbar states
    private val _currentPosition = MutableStateFlow(0L)
    private val _bufferedPercentage = MutableStateFlow(0)

    val currentPosition = _currentPosition.asStateFlow()
    val bufferedPercentage = _bufferedPercentage.asStateFlow()

    // Local states
    private val _lyricsReady = MutableStateFlow(false)
    private val _dictionaryReady = MutableStateFlow(false)
    private val _audioReady = MutableStateFlow(false)

    private val cues = _cues.asStateFlow()
    private val lyrics = _lyrics.asStateFlow()
    private val parser = _parser.asStateFlow()

    val playIcon = _playIcon.asStateFlow()
    val player = _player.asStateFlow()
    val currentCue = _currentCue.asStateFlow()
    val playerStatus = _playerStatus.asStateFlow()
    val finalized = _finalized.asStateFlow()
    val isBuffering = _isBuffering.asStateFlow()

    private fun checkValuesReady() {
        if (_audioReady.value && _lyricsReady.value && _dictionaryReady.value) {
            setPlayerStatus(PlayerStatus.READY)
        }
    }

    init {
        viewModelScope.launch { _audioReady.collect { if (it) checkValuesReady() } }
        viewModelScope.launch { _lyricsReady.collect { if (it) checkValuesReady() } }
        viewModelScope.launch { _dictionaryReady.collect { if (it) checkValuesReady() } }
    }

    fun getLyrics(
        preferences: Preferences,
        context: Context,
        id: String,
        lang: Language,
        video: VideoStreamInterface
    ) {
        viewModelScope.launch {
            playerRepository.init(context)
            playerRepository.endpoint = preferences.getOrDefault("api_endpoint")
            playerRepository.onGetLyricsSuccess = {
                val jsonObject = JsonParser.parseString(it).asJsonObject

                val lyricks = jsonObject.get("lyrics").asString
                val dictionary = jsonObject.getAsJsonArray("dictionary").toString()

                lyricsParse(preferences["filter_romanizations"] ?: true, lyricks)
                setCues(lyrics.value)

                _lyricsReady.update { true }

                initParser(lang)
                addDictionary(lang.dictionaryName, dictionary)

                for (cue in lyrics.value) {
                    val wordsFound = parser.value.findWords(cue.text)

                    for (word in wordsFound) {
                        cue.words.add(word)
                        preloadImage(word.parent, playerRepository.endpoint, context)
                    }
                }

                _dictionaryReady.update { true }
            }

            playerRepository.getLyrics(id, lang.tag, video)
        }
    }

    private fun preloadImage(parent: String, endpoint: String, context: Context) {
        viewModelScope.launch {
            val loader = ImageLoader(context)

            withContext(Dispatchers.IO) {
                val request = ImageRequest.Builder(context)
                    .data("$endpoint/api/v1/image?parent=$parent")
                    .allowHardware(false)
                    .build()

                loader.execute(request)
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

                    for (cue in cues.value) {
                        for (word in cue.words) {
                            WordReviseViewModel.appendWord(ReviseWord(word))
                        }
                    }

                    _finalized.update { true }
                }
            }

            player.value.apply {
                onPositionChange = { pos, bufferedPercentage ->
                    setCurrentCue(lyrics.value.getCueAt(pos))
                    _currentPosition.update { pos.toLong() }
                    _bufferedPercentage.update { bufferedPercentage }
                }
                onInitializeFail = { setPlayerStatus(PlayerStatus.ERROR) }
                onPrepared = { _audioReady.update { true } }

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