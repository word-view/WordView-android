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

import cc.wordview.app.api.ApiRequestRepository
import cc.wordview.app.extractor.VideoStreamInterface
import com.google.gson.JsonParser

interface PlayerRepository : ApiRequestRepository {
    var onSucceed: (String, String) -> Unit
    var onFail: (String, Int) -> Unit

    fun getLyrics(id: String, lang: String, video: VideoStreamInterface)

    fun parseLyricsAndDictionary(res: String): Pair<String, String> {
        val jsonObject = JsonParser.parseString(res).asJsonObject

        val lyrics = jsonObject.get("lyrics").asString
        val dictionary = jsonObject.getAsJsonArray("dictionary").toString()

        return lyrics to dictionary
    }
}