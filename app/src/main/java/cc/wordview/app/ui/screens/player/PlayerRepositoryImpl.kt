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

import cc.wordview.app.api.APIUrl
import cc.wordview.app.api.Response
import cc.wordview.app.extensions.asURLEncoded
import cc.wordview.app.extractor.VideoStreamInterface
import com.android.volley.RequestQueue
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor() : PlayerRepository {
    override var onGetLyricsSuccess: (String, String) -> Unit =
        { _: String, _: String -> }

    override var onGetLyricsFail: (String) -> Unit = {}

    override lateinit var queue: RequestQueue

    override fun getLyrics(id: String, lang: String, video: VideoStreamInterface) {
        val url = APIUrl("$endpoint/api/v1/lyrics")

        url.addRequestParam("id", id)
        url.addRequestParam("lang", lang)
        url.addRequestParam("trackName", video.cleanTrackName.asURLEncoded())
        url.addRequestParam("artistName", video.cleanArtistName.asURLEncoded())

        val response = Response({
            val (lyrics, dictionary) = parseLyricsAndDictionary(it)
            onGetLyricsSuccess(lyrics, dictionary)
        }, {
            val statusCode = it.networkResponse?.statusCode
            val responseData = it.networkResponse?.data?.let { String(it) }
            val errorTitle = scrapeErrorFromResponseData(responseData)

            onGetLyricsFail(
                it.message ?: "Request failed with status code $statusCode\n$errorTitle"
            )
        })

        val request = jsonRequest(url.getURL(), response)

        request.setRetryPolicy(highTimeoutRetryPolicy)

        queue.add(request)
    }

    private fun scrapeErrorFromResponseData(responseData: String?): String? {
        if (responseData != null) {
            val titleRegex = "<title>(.*?)</title>".toRegex(RegexOption.IGNORE_CASE)
            val matchResult = titleRegex.find(responseData)
            return matchResult?.groups?.get(1)?.value
        } else return null
    }
}