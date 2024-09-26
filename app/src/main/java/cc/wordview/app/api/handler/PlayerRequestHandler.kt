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

package cc.wordview.app.api.handler

import android.content.Context
import android.util.Log
import cc.wordview.app.SongViewModel
import cc.wordview.app.api.JsonAcceptingRequest
import cc.wordview.app.api.Response
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.net.URLEncoder

object PlayerRequestHandler {
    private val TAG = PlayerRequestHandler::class.java.simpleName

    private lateinit var queue: RequestQueue

    var endpoint: String = "10.0.2.2"

    var onLyricsSucceed: (lyrics: String) -> Unit = {}
    var onDictionarySucceed: (dictionary: String) -> Unit = {}

    fun init(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    fun getLyricsWordView(query: String) {
        if (query == "") return

        val url = "http://$endpoint:8080/api/v1/music/lyrics/find?title=${URLEncoder.encode(query)}"

        val response = Response({ onLyricsSucceed(it) }, {
            Log.e(TAG, "getLyricsWordView:", it)
        })

        val lyricsRequest = lyricsRequest(url, response)

        lyricsRequest.setRetryPolicy(
            DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        queue.add(lyricsRequest)
    }

    fun getLyricsYoutube(url: String) {
        val lyricsRequest = lyricsRequest(
            url,
            Response({ onLyricsSucceed(it) }, {
                Log.e(TAG, "getLyricsYoutube:", it)
                getLyricsWordView(SongViewModel.video.value.searchQuery)
            })
        )
        queue.add(lyricsRequest)
    }

    private fun lyricsRequest(url: String, handler: Response): StringRequest {
        Log.d(TAG, "Requesting lyrics from $url")

        return StringRequest(
            Request.Method.GET,
            url,
            { handler.onSuccessResponse(it) },
            { handler.onErrorResponse(it) })
    }

    fun getDictionary(dictionary: String) {
        Log.d(TAG, "Requesting dictionary \"$dictionary\"")

        val url = "http://$endpoint:8080/api/v1/dictionary?lang=$dictionary"

        val response = Response({ onDictionarySucceed(it) }, {
            Log.e(TAG, "getDictionary:", it)
        })

        val jsonRequest = JsonAcceptingRequest(Request.Method.GET,
            url,
            { response.onSuccessResponse(it) },
            { response.onErrorResponse(it) })

        queue.add(jsonRequest)
    }
}