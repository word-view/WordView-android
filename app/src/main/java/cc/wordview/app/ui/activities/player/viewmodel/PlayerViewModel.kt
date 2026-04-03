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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.wordview.app.BuildConfig
import cc.wordview.app.components.media.AudioPlayer
import cc.wordview.app.components.media.AudioPlayerListener
import cc.wordview.app.database.RoomAccess
import cc.wordview.app.components.extensions.toSeconds
import cc.wordview.app.extractor.VideoStream
import cc.wordview.app.extractor.VideoStreamInterface
import cc.wordview.app.components.media.caption.Lyrics
import cc.wordview.app.components.media.caption.WordViewCue
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.Parser
import coil3.request.ImageRequest
import coil3.request.allowHardware
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _playIcon = MutableStateFlow(Icons.Filled.PlayArrow)
    private val _lyrics = MutableStateFlow(Lyrics("", Parser(Language.ENGLISH)))
    private val _parser = MutableStateFlow(Parser(Language.ENGLISH))
    private val _player = MutableStateFlow(AudioPlayer())
    private val _currentCue = MutableStateFlow(WordViewCue())
    private val _playerState = MutableStateFlow(PlayerState.LOADING)
    private val _finalized = MutableStateFlow(false)
    private val _isBuffering = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow("")
    private val _statusCode = MutableStateFlow(0)

    private val _videoStream = MutableStateFlow<VideoStreamInterface>(VideoStream())

    // Seekbar states
    private val _currentPosition = MutableStateFlow(0L)
    private val _bufferedPercentage = MutableStateFlow(0)

    val currentPosition = _currentPosition.asStateFlow()
    val bufferedPercentage = _bufferedPercentage.asStateFlow()

    val playIcon = _playIcon.asStateFlow()
    val player = _player.asStateFlow()
    val currentCue = _currentCue.asStateFlow()
    val playerState = _playerState.asStateFlow()
    val finalized = _finalized.asStateFlow()
    val isBuffering = _isBuffering.asStateFlow()
    val errorMessage = _errorMessage.asStateFlow()
    val statusCode = _statusCode.asStateFlow()
    val videoStream = _videoStream.asStateFlow()

    private val viewedVideoDao = RoomAccess.getDatabase().viewedVideoDao()

    // tracks the steps to consider that the player is
    // prepared to start playing (audio ready, lyrics ready, dictionary ready)
    private val stepsReady = MutableStateFlow(0)

    private fun computeAndCheckReady() {
        stepsReady.update { it + 1 }
        if (stepsReady.value == 3)
            setPlayerState(PlayerState.READY)
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
            initParser(lang)
            addDictionary(lang.dictionaryName, dictionary)

            parseLyrics(lyrics)
            computeAndCheckReady()

            preloadImages()
        }

        playerRepository.getLyrics(id, lang.tag, video)
    }

    private fun preloadImages() {
        for (cue in _lyrics.value) {
            for (word in cue.words) {
                enqueueImage(word.parent)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            ImageCacheManager.onQueueCompleted = { computeAndCheckReady() }
            ImageCacheManager.executeAllInQueue()
        }
    }

    private fun enqueueImage(parent: String) = viewModelScope.launch(Dispatchers.IO) {
        if (parent == "") return@launch

        val request = ImageRequest.Builder(appContext)
            .data("${BuildConfig.API_BASE_URL}/api/v1/image?parent=$parent")
            .allowHardware(true)
            .memoryCacheKey(parent)

        ImageCacheManager.enqueue(request)
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

    /**
     * Updates the watchedUntil of the song to the current position of the player
     */
    fun saveCurrentPosition() = viewModelScope.launch(Dispatchers.IO) {
        // unless something really odd happens the last saved history should be
        // always the song that the player is reproducing
        val song = viewedVideoDao.getAll().last()

        val position = currentPosition.value.toSeconds()

        if (position > 0L) {
            Timber.i("Saving the current position '$position' to the watched history")
            viewedVideoDao.updateWatchedUntil(song.uid, position)
        }
    }

    private fun playIconPause() {
        _playIcon.update { Icons.Filled.PlayArrow }
    }

    private fun playIconPlay() {
        _playIcon.update { Icons.Filled.Pause }
    }

    private fun parseLyrics(lyrics: String) {
        _lyrics.update { Lyrics(lyrics, _parser.value) }
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

    fun setVideoStream(videoStream: VideoStreamInterface) {
        _videoStream.update { videoStream }
    }
}