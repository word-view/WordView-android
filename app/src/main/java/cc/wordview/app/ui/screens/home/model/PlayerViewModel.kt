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

package cc.wordview.app.ui.screens.home.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import cc.wordview.app.subtitle.Lyrics
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.Parser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object PlayerViewModel : ViewModel() {
    private val _cues = MutableStateFlow(ArrayList<WordViewCue>())
    private val _highlightedCuePosition = MutableStateFlow(0)
    private val _playIcon = MutableStateFlow(Icons.Filled.PlayArrow)
    private val _lyrics = MutableStateFlow(Lyrics())
    private val _parser = MutableStateFlow(Parser(Language.ENGLISH))

    val cues: StateFlow<ArrayList<WordViewCue>> = _cues.asStateFlow()
    val highlightedCuePosition: StateFlow<Int> = _highlightedCuePosition.asStateFlow()
    val playIcon: StateFlow<ImageVector> = _playIcon.asStateFlow()
    val lyrics: StateFlow<Lyrics> = _lyrics.asStateFlow()
    val parser: StateFlow<Parser> = _parser.asStateFlow()

    fun setCues(cues: ArrayList<WordViewCue>) {
        _cues.update { cues }
    }

    fun clearCues() {
        _cues.update { ArrayList() }
    }

    fun highlightCueAt(position: Int) {
        _highlightedCuePosition.update { position }
    }

    fun unhighlightCues() {
        _highlightedCuePosition.update { 0 }
    }

    fun playIconPause() {
        _playIcon.update { Icons.Filled.PlayArrow }
    }

    fun playIconPlay() {
        _playIcon.update { Icons.Filled.Pause }
    }

    fun lyricsParse(res: String) {
        val newLyrics = Lyrics()
        newLyrics.parse(res)
        _lyrics.update { newLyrics }
    }

    fun initParser(language: Language) {
        _parser.update { Parser(language) }
    }

    fun addDictionary(name: String, dictionary: String) {
        _parser.value.addDictionary(name, dictionary)
    }
}