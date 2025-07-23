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

package cc.wordview.app.ui.activities.player.viewmodel

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.wordview.app.BuildConfig
import cc.wordview.app.api.APIUrl
import cc.wordview.app.api.getStoredJwt
import cc.wordview.app.api.request.AuthenticatedStringRequest
import cc.wordview.app.audio.AudioPlayerListener
import cc.wordview.app.audio.AudioPlayer
import cc.wordview.app.extractor.VideoStreamInterface
import cc.wordview.app.subtitle.Lyrics
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.misc.PlayerToLessonCommunicator
import cc.wordview.app.ui.activities.lesson.ReviseTimer
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.activities.lesson.viewmodel.ReviseWord
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.Parser
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.android.volley.toolbox.Volley
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val playerRepository: PlayerRepository,
    private val knownWordsRepository: KnownWordsRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _playIcon = MutableStateFlow(Icons.Filled.PlayArrow)
    private val _cues = MutableStateFlow(ArrayList<WordViewCue>())
    private val _lyrics = MutableStateFlow(Lyrics(""))
    private val _parser = MutableStateFlow(Parser(Language.ENGLISH))
    private val _player = MutableStateFlow(AudioPlayer())
    private val _currentCue = MutableStateFlow(WordViewCue())
    private val _playerState = MutableStateFlow(PlayerState.LOADING)
    private val _finalized = MutableStateFlow(false)
    private val _isBuffering = MutableStateFlow(false)
    private val _notEnoughWords = MutableStateFlow(false)
    private val _noTimeLeft = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow("")
    private val _statusCode = MutableStateFlow(0)
    private val _knownWords = MutableStateFlow(ArrayList<String>())

    // Seekbar states
    private val _currentPosition = MutableStateFlow(0L)
    private val _bufferedPercentage = MutableStateFlow(0)

    val currentPosition = _currentPosition.asStateFlow()
    val bufferedPercentage = _bufferedPercentage.asStateFlow()

    val playIcon = _playIcon.asStateFlow()
    val player = _player.asStateFlow()
    val currentCue = _currentCue.asStateFlow()
    val cues = _cues.asStateFlow()
    val playerState = _playerState.asStateFlow()
    val finalized = _finalized.asStateFlow()
    val isBuffering = _isBuffering.asStateFlow()
    val notEnoughWords = _notEnoughWords.asStateFlow()
    val noTimeLeft = _noTimeLeft.asStateFlow()
    val errorMessage = _errorMessage.asStateFlow()
    val statusCode = _statusCode.asStateFlow()

    // tracks the steps to consider that the player is
    // prepared to start playing (audio ready, lyrics ready, dictionary ready)
    private val stepsReady = MutableStateFlow(0)

    private fun computeAndCheckReady() {
        stepsReady.update { it + 1 }
        if (stepsReady.value == 3)
            setPlayerState(PlayerState.READY)
    }

    fun getKnownWords(lang: Language) = viewModelScope.launch {
        val jwt = getStoredJwt(appContext)

        knownWordsRepository.apply {
            onFail = { message, status ->
                Timber.e("Failed to request known words \n\tmessage=$message, status=$status")
            }

            onSucceed = {
                for (word in it)
                    _knownWords.value.add(word)
            }

            jwt?.let { getKnownWords(lang.tag, it) }
        }
    }

    fun getLessonTime() {
        val jwt = getStoredJwt(appContext) ?: return
        val queue = Volley.newRequestQueue(appContext)
        val url = APIUrl("${BuildConfig.API_BASE_URL}/api/v1/user/me/lesson_time")

        val request = AuthenticatedStringRequest(
            url.getURL(),
            jwt,
            onSuccess = {
                Timber.i("Time left: $it")
                ReviseTimer.timeRemaining = it.toLong()
                        },
            onError = { message, status -> Timber.e("Failed to retrieve lesson time: \n\tmessage=$message, status=$status") }
        )

        queue.add(request)
    }

    fun getLyrics(
        id: String,
        lang: Language,
        video: VideoStreamInterface
    ) = viewModelScope.launch {
        playerRepository.onFail = { message, status ->
            _errorMessage.update { message }
            _statusCode.update { status }
            setPlayerState(PlayerState.ERROR)
        }
        playerRepository.onSucceed = { lyrics, dictionary ->
            parseLyrics(lyrics)
            setCues(_lyrics.value)

            computeAndCheckReady()

            initParser(lang)
            addDictionary(lang.dictionaryName, dictionary)

            val words = ArrayList<String>()

            for (cue in _lyrics.value) {
                val wordsFound = _parser.value.findWords(cue.text)

                for (word in wordsFound) {
                    preloadImage(word.parent)
                    words.add(word.word)
                    cue.words.add(word)
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                ImageCacheManager.onQueueCompleted = { computeAndCheckReady() }
                ImageCacheManager.executeAllInQueue()
            }
        }

        playerRepository.getLyrics(id, lang.tag, video)
    }

    private fun preloadImage(parent: String) = viewModelScope.launch {
        if (parent == "") return@launch

        withContext(Dispatchers.IO) {
            val request = ImageRequest.Builder(appContext)
                .data("${BuildConfig.API_BASE_URL}/api/v1/image?parent=$parent")
                .allowHardware(true)
                .memoryCacheKey(parent)

            ImageCacheManager.enqueue(request)
        }
    }

    fun initAudio(videoStreamUrl: String) = viewModelScope.launch {
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
                        if (word.parent == "") continue

                        val reviseWord = ReviseWord(word)

                        reviseWord.isKnown = _knownWords.value.contains(reviseWord.tokenWord.parent)

                        PlayerToLessonCommunicator.appendWord(reviseWord)
                    }
                }

                val isTimerFinished = ReviseTimer.timeRemaining == 0L
                val wordsToRevise = PlayerToLessonCommunicator.wordsToRevise.value

                if (isTimerFinished) {
                    _noTimeLeft.update { true }
                } else if (wordsToRevise.isEmpty() || wordsToRevise.size < 3) {
                    _notEnoughWords.update { true }
                } else {
                    _finalized.update { true }
                }
            }
        }

        player.value.apply {
            onPositionChange = { pos, bufferedPercentage ->
                setCurrentCue(_lyrics.value.getCueAt(pos))
                _currentPosition.update { pos.toLong() }
                _bufferedPercentage.update { bufferedPercentage }
            }
            onInitializeFail = { setPlayerState(PlayerState.ERROR) }
            onPrepared = { computeAndCheckReady() }

            initialize(videoStreamUrl, appContext, listener)
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

    private fun parseLyrics(lyrics: String) {
        _lyrics.update { Lyrics(lyrics) }
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

    fun setPlayerState(playerState: PlayerState) {
        _playerState.update { playerState }
    }

    fun setErrorMessage(message: String) {
        _errorMessage.update { message }
    }
}