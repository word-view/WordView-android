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
import cc.wordview.app.components.media.AudioPlayerListener
import cc.wordview.app.database.RoomAccess
import cc.wordview.app.components.extensions.toSeconds
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
    private val _state = MutableStateFlow(PlayerState())
    private val _errorState = MutableStateFlow(PlayerErrorState())

    val state = _state.asStateFlow()
    val errorState = _errorState.asStateFlow()

    private val _currentCue = MutableStateFlow(WordViewCue())
    private val _currentPosition = MutableStateFlow(0L)
    private val _bufferedPercentage = MutableStateFlow(0)
    private val _isBuffering = MutableStateFlow(false)
    private val _ready = MutableStateFlow(false)


    val currentCue = _currentCue.asStateFlow()
    val currentPosition = _currentPosition.asStateFlow()
    val bufferedPercentage = _bufferedPercentage.asStateFlow()
    val isBuffering = _isBuffering.asStateFlow()
    val ready = _ready.asStateFlow()


    private var parser = Parser(Language.ENGLISH)

    private val viewedVideoDao = RoomAccess.getDatabase().viewedVideoDao()

    var lyricsReady: Boolean = false
    var playerReady: Boolean = false
    var imagesReady: Boolean = false

    /**
     * Checks if everything is ready and if so sets the `_ready` to true
     */
    private fun checkReady() {
        if (lyricsReady && playerReady && imagesReady) {
            _ready.update { true }
        }
    }

    fun getLyrics(
        id: String,
        lang: Language,
        video: VideoStreamInterface
    ) = viewModelScope.launch {
        playerRepository.onFail = { message, status ->
            declarePlayerError(PlayerErrorState(message, status))
        }
        playerRepository.onSucceed = { lyrics, dictionary ->
            parser = Parser(lang)
            parser.addDictionary(lang.dictionaryName, dictionary)

            parseLyrics(lyrics)

            lyricsReady = true
            checkReady()

            preloadImages()
        }

        playerRepository.getLyrics(id, lang.tag, video)
    }

    fun getSubtitle(
        id: String,
        lang: Language
    ) = viewModelScope.launch {
        playerRepository.onFail = { message, status ->
            // ignore errors allowing the player to follow normally
            lyricsReady = true
            // ready images as well
            imagesReady = true

            checkReady()
        }
        playerRepository.onSucceed = { subtitle, dictionary ->
            parser = Parser(lang)
            parser.addDictionary(lang.dictionaryName, dictionary)

            parseLyrics(subtitle)

            lyricsReady = true
            checkReady()

            preloadImages()
        }

        playerRepository.getSubtitles(id, lang.tag)
    }

    private fun preloadImages() {
        for (cue in _state.value.lyrics) {
            for (word in cue.words) {
                enqueueImage(word.parent)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            ImageCacheManager.onQueueCompleted = {
                imagesReady = true
                checkReady()
            }
            ImageCacheManager.executeAllInQueue()
        }
    }

    private fun enqueueImage(parent: String) {
        if (parent == "") return

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
                _state.value.player.stop()
            }
        }

        _state.value.player.apply {
            onPositionChange = { pos, bufferedPercentage ->
                setCurrentCue(_state.value.lyrics.getCueAt(pos))
                _currentPosition.update { pos.toLong() }
                _bufferedPercentage.update { bufferedPercentage }
            }
            onInitializeFail = { setDisplay(Display.ERROR) }
            onPrepared = {
                playerReady = true
                checkReady()
            }

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
        _state.update { it.copy(playIcon = Icons.Filled.PlayArrow) }
    }

    private fun playIconPlay() {
        _state.update { it.copy(playIcon = Icons.Filled.Pause) }
    }

    private fun parseLyrics(lyrics: String) {
        _state.update { it.copy(lyrics = Lyrics(lyrics, parser)) }
    }

    private fun setCurrentCue(cue: WordViewCue) {
        _currentCue.update { cue }
    }

    fun setDisplay(display: Display) {
        _state.update { it.copy(display = display) }
    }

    /**
     * Declares the error that has happened and directions the player to show it
     */
    fun declarePlayerError(errorState: PlayerErrorState) {
        _errorState.update { errorState }
        _state.update { it.copy(display = Display.ERROR) }
    }

    /**
     * Performs session cleanups
     */
    fun cleanup() {
        _state.update { PlayerState() }
    }
}