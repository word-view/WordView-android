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

package cc.wordview.app.api.request

import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import timber.log.Timber

/**
 * Generic authentication request, encompasses both login and registration.
 */
class AuthRequest(
    url: String?,
    private val body: JSONObject,
    onSuccess: (jwt: String) -> Unit,
    onError: (status: Int?, message: String?) -> Unit,
) : StringRequest(Method.POST, url, {
    onSuccess(it)
}, {
    val statusCode = it.networkResponse?.statusCode
    val responseData = it.networkResponse?.data?.let { String(it) }

    onError(statusCode, responseData)
}) {
    init {
        Timber.v("init: method=POST, url=$url, onSuccess=$onSuccess, onError=$onError")

        retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    override fun getBodyContentType(): String {
        return "application/json"
    }

    override fun getBody(): ByteArray {
        return body.toString().toByteArray()
    }
}