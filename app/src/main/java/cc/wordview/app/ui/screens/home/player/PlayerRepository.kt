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
import cc.wordview.app.api.JsonAcceptingRequest
import cc.wordview.app.api.Response
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import javax.inject.Inject

class PlayerRepository @Inject constructor() {
    private val TAG = this::class.java.simpleName

    var endpoint: String = "10.0.2.2"

    private lateinit var queue: RequestQueue

    fun init(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    var onGetLyricsSuccess: (String) -> Unit = {}

    fun getLyrics(id: String, lang: String, query: String) {
        val url = "http://$endpoint:8080/api/v1/lyrics?id=$id&lang=$lang&query=$query"

        val response = Response({ onGetLyricsSuccess(it) }, { Log.e(TAG, "getLyrics: ", it) })

        val request = jsonRequest(url, response)

        request.setRetryPolicy(
            DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        queue.add(request)
    }

    private fun jsonRequest(url: String, handler: Response): StringRequest {
        return JsonAcceptingRequest(
            Request.Method.GET,
            url,
            { handler.onSuccessResponse(it) },
            { handler.onErrorResponse(it) })
    }
}