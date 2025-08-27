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
import cc.wordview.app.api.APIUrl
import cc.wordview.app.api.request.LyricsRequest
import cc.wordview.app.components.extensions.asURLEncoded
import cc.wordview.app.extractor.VideoStreamInterface
import com.android.volley.toolbox.Volley
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayerRepository {
    override var onSucceed: (String, String) -> Unit =
        { _: String, _: String -> }

    override var onFail: (String, Int) -> Unit = { message, status -> }

    override var queue = Volley.newRequestQueue(context)

    override fun getLyrics(id: String, lang: String, video: VideoStreamInterface) {
        val url = APIUrl("$endpoint/api/v1/lyrics")

        url.addRequestParam("id", id)
        url.addRequestParam("lang", lang)
        url.addRequestParam("trackName", video.cleanTrackName.asURLEncoded())
        url.addRequestParam("artistName", video.cleanArtistName.asURLEncoded())

        val request = LyricsRequest(
            url.getURL(),
            { lyrics, dictionary -> onSucceed(lyrics, dictionary) },
            { message, status -> onFail(message, status) }
        )

        queue.add(request)
    }
}