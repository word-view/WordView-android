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
import cc.wordview.app.BuildConfig
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

interface ApiRequestRepository {
    val endpoint get() = BuildConfig.API_BASE_URL
    var queue: RequestQueue

    val highTimeoutRetryPolicy: DefaultRetryPolicy
        get() = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

    fun init(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    fun jsonRequest(url: String, handler: Response): StringRequest {
        Log.d(this::class.simpleName, "GET: $url")

        return JsonAcceptingRequest(
            Request.Method.GET,
            url,
            { handler.onSuccessResponse(it) },
            { handler.onErrorResponse(it) })
    }
}