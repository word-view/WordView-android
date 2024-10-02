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

package cc.wordview.app.ui.screens.home.player

import android.content.Context
import android.util.Log
import cc.wordview.app.SongViewModel
import cc.wordview.app.api.Video
import cc.wordview.app.api.handler.PlayerRequestHandler
import cc.wordview.app.subtitle.getIconForWord
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel
import cc.wordview.app.ui.screens.home.revise.components.ReviseWord
import cc.wordview.gengolex.Language
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.Preferences
import kotlin.reflect.typeOf

/**
 * Manages the state and logic for the player screen.
 *
 * This class handles the initialization and setup of the audio player, lyrics, and dictionary,
 * as well as managing the state of these components.
 *
 * @param preferences Compose preferences instance.
 *
 */
class PlayerState(private val preferences: Preferences) {
    private val TAG = PlayerState::class.java.simpleName

    private val playerStateCoroutine = CoroutineScope(Dispatchers.IO)

    private val viewModel = PlayerViewModel
    private val requestHandler = PlayerRequestHandler

    private val player = viewModel.player.value

    private val _audioReady = MutableStateFlow(false)
    private val _lyricsReady = MutableStateFlow(false)
    private val _dictionaryReady = MutableStateFlow(false)

    private fun checkValuesReady() {
        if (_audioReady.value && _lyricsReady.value && _dictionaryReady.value) {
            viewModel.setPlayerStatus(PlayerStatus.READY)
        }
    }

    fun setup(cleanup: () -> Unit, context: Context) {
        playerStateCoroutine.launch {
            _audioReady.collect { if (it) checkValuesReady() }
        }
        playerStateCoroutine.launch {
            _lyricsReady.collect { if (it) checkValuesReady() }
        }
        playerStateCoroutine.launch {
            _dictionaryReady.collect { if (it) checkValuesReady() }
        }


        playerStateCoroutine.launch {
            player.apply {
                onPositionChange = {
                    viewModel.setCurrentCue(viewModel.lyrics.value.getCueAt(it))
                }
                onInitializeFail = { viewModel.setPlayerStatus(PlayerStatus.ERROR) }
                onPrepared = { _audioReady.update { true } }

                setOnCompletionListener {
                    for (cue in viewModel.cues.value) {
                        for (word in cue.words) {
                            if (getIconForWord(word.parent) != null) {
                                WordReviseViewModel.appendWord(ReviseWord(word))
                            }
                        }
                    }

                    cleanup()
                    viewModel.finalize()
                }
            }

            player.initialize(SongViewModel.videoStream.value.getStreamURL())
        }

        playerStateCoroutine.launch {
            requestHandler.apply {
                endpoint = preferences["api_endpoint"] ?: "10.0.2.2"

                onLyricsSucceed = {
                    val jsonObject = JsonParser.parseString(it).asJsonObject

                    val lyrics = jsonObject.get("lyrics").asString
                    val dictionary = jsonObject.getAsJsonArray("dictionary").toString()

                    viewModel.lyricsParse(lyrics)
                    viewModel.setCues(viewModel.lyrics.value)

                    _lyricsReady.update { true }

                    viewModel.initParser(Language.JAPANESE)
                    viewModel.addDictionary("kanji", dictionary)

                    for (cue in viewModel.lyrics.value) {
                        val wordsFound = viewModel.parser.value.findWords(cue.text)

                        for (word in wordsFound) {
                            cue.words.add(word)
                        }
                    }

                    _dictionaryReady.update { true }
                }

                init(context)
            }

            val filter = preferences["filter_romanizations"] ?: true
            viewModel.setFilterRomanizations(filter)

            val video = SongViewModel.videoStream.value

            requestHandler.getLyrics(video.info.id, "ja", video.searchQuery)
        }
    }
}