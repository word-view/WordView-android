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

import com.android.volley.VolleyError
import timber.log.Timber

class Response(
    private val onSuccess: (res: String) -> Unit,
    private val onError: (err: VolleyError) -> Unit
) {
    fun onSuccessResponse(response: String?) {
        if (response != null) {
            onSuccess.invoke(response)
        }
    }

    fun onErrorResponse(error: VolleyError) {
        // Abstain from showing the exception when a 404 happens
        if (error.networkResponse?.statusCode != 404)
            Timber.e("Request failed: ", error)

        onError.invoke(error)
    }
}