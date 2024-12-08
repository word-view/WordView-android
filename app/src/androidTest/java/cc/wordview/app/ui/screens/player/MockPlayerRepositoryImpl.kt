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

import cc.wordview.app.extractor.VideoStreamInterface
import com.android.volley.RequestQueue
import com.google.gson.JsonParser
import javax.inject.Inject

class MockPlayerRepositoryImpl @Inject constructor() : PlayerRepository {
    override var onGetLyricsSuccess: (String, String) -> Unit = { _: String, _: String -> }
    override var onGetLyricsFail: (String) -> Unit = {}

    override var endpoint: String = ""

    override lateinit var queue: RequestQueue

    override fun getLyrics(id: String, lang: String, video: VideoStreamInterface) {
        if (mocklyrics == "fail_trigger") {
            onGetLyricsFail(mocklyrics)
        } else {
            val (lyrics, dictionary) = parseLyricsAndDictionary(mocklyrics)
            onGetLyricsSuccess(lyrics, dictionary)
        }
    }
}