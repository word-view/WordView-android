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
import cc.wordview.app.api.Response
import cc.wordview.app.api.apiURL
import cc.wordview.app.audio.AudioPlayer
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.net.URLEncoder

object PlayerRequestHandler {
    private val TAG = PlayerRequestHandler::class.java.simpleName
    private lateinit var queue: RequestQueue

    var successResponse: (res: String) -> Unit = { res ->
        PlayerViewModel.lyricsParse(res)
        PlayerViewModel.setCues(PlayerViewModel.lyrics.value)
        AudioPlayer.togglePlay()
    }

    var onGetLyricsFail: () -> Unit = {}
    var onWordFindFail: () -> Unit = {}

    private val wordFindHandler = Response(successResponse) {
        Log.e(TAG, "getLyricsWordFind failed!")
        onWordFindFail()
    }

    private var getLyricsHandler = Response(successResponse) {
        Log.e(TAG, "getLyrics failed! Retrying with wordFind")
        onGetLyricsFail()
        getLyricsWordFind(SongViewModel.video.value.searchQuery)
    }

    fun init(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    private fun getLyricsWordFind(searchQuery: String) {
        Log.d(TAG, "Searching for \"$searchQuery\" using WordFind")

        val url = "$apiURL/music/lyrics/find?title=${URLEncoder.encode(searchQuery)}"

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            wordFindHandler.onSuccessResponse(response)
        }, { err -> wordFindHandler.onErrorResponse(err) })

        stringRequest.setRetryPolicy(
            DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        queue.add(stringRequest)
    }

    fun getLyrics(id: String, lang: String) {
        Log.d(TAG, "Looking for lyrics for video with id \"$id\" and language \"$lang\" ")

        val url = "$apiURL/music/lyrics?id=$id&lang=$lang"

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            getLyricsHandler.onSuccessResponse(response)
        }, { err -> getLyricsHandler.onErrorResponse(err) })

        queue.add(stringRequest)
    }
}