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

package cc.wordview.app.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

private const val TAG = "Music"

interface APICallback {
    fun onSuccessResponse(response: String?)
    fun onErrorResponse(response: String?)
}

fun getHistory(callback: APICallback, context: Context) {
    val queue = Volley.newRequestQueue(context)
    val url = "$apiURL/music/history"

    val stringRequest =
        StringRequest(Request.Method.GET, url, { response ->
            callback.onSuccessResponse(response)
        },
            { err -> Log.e(TAG, "Request failed: ${err.message}") })

    queue.add(stringRequest)
}

fun getLyrics(id: String, lang: String, callback: APICallback, context: Context) {
    val queue = Volley.newRequestQueue(context)
    val url = "$apiURL/music/lyrics?id=$id&lang=$lang"

    val stringRequest =
        StringRequest(Request.Method.GET, url, { response ->
            callback.onSuccessResponse(response)
        },
            { err -> Log.e(TAG, "Request failed: ${err.message}") })

    queue.add(stringRequest)
}