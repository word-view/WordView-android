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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import cc.wordview.app.components.media.AudioPlayer
import cc.wordview.app.components.media.caption.Lyrics
import cc.wordview.app.extractor.VideoStream
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.Parser

/**
 * The interface states of player, these are not updated constantly so separating
 * them into a single class does not offer a noticeable performance prejudice.
 */
data class PlayerUIState(
    val playIcon: ImageVector = Icons.Filled.PlayArrow,
    val lyrics: Lyrics = Lyrics("", Parser(Language.ENGLISH)),
    val player: AudioPlayer = AudioPlayer(),
    val loadState: LoadState = LoadState.LOADING,
    val finalized: Boolean = false,
    val videoStream: VideoStream = VideoStream(),
)
