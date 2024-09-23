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
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.net.URLEncoder

object PlayerRequestHandler {
    private val TAG = PlayerRequestHandler::class.java.simpleName

    private val viewModel = PlayerViewModel
    private lateinit var queue: RequestQueue

    var onLyricsSucceed: (lyrics: String) -> Unit = {}
    var onDictionarySucceed: () -> Unit = {}

    var endpoint: String = "10.0.2.2"

    val onGetDictionariesSucceed: (res: String) -> Unit = {
        PlayerViewModel.addDictionary("kanji", it)

        for (cue in viewModel.lyrics.value) {
            val wordsFound = viewModel.parser.value.findWords(cue.text)

            for (word in wordsFound) {
                cue.words.add(word)
            }
        }

        onDictionarySucceed()
    }

    private val wordFindHandler = Response({ onLyricsSucceed(it) }) {
        Log.e(TAG, "Failed to find lyrics using WordFind", it)
    }

    private var getLyricsHandler = Response({ onLyricsSucceed(it) }) {
        Log.e(TAG, "Failed to find lyrics", it)
        getLyricsWordFind(SongViewModel.video.value.searchQuery)
    }

    private val getDictionariesHandler = Response({ onGetDictionariesSucceed(it) }) {
        Log.e(TAG, "Failed to retrieve a dictionary", it)
    }

    fun init(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    fun getLyricsWordFind(searchQuery: String) {
        Log.d(TAG, "Searching for \"$searchQuery\" using WordFind")

        val url = "http://$endpoint:8080/api/v1/music/lyrics/find?title=${URLEncoder.encode(searchQuery)}"

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { wordFindHandler.onSuccessResponse(it) },
            { wordFindHandler.onErrorResponse(it) })

        stringRequest.setRetryPolicy(
            DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        queue.add(stringRequest)
    }

    fun getLyrics(url: String) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { getLyricsHandler.onSuccessResponse(it) },
            { getLyricsHandler.onErrorResponse(it) })

        queue.add(stringRequest)
    }

    fun getDictionary(dictionary: String) {
        Log.d(TAG, "Requesting a dictionary called \"$dictionary\"")

        val url = "http://$endpoint:8080/api/v1/dictionary?lang=$dictionary"

        val jsonRequest = JsonAcceptingRequest(Request.Method.GET,
            url,
            { getDictionariesHandler.onSuccessResponse(it) },
            { getDictionariesHandler.onErrorResponse(it) })

        queue.add(jsonRequest)
    }
}