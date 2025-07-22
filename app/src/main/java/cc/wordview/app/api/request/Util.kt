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

import com.android.volley.VolleyError

fun getErrorResults(error: VolleyError): Pair<Int, String> {
    val networkResponse = error.networkResponse

    val statusCode = networkResponse?.statusCode ?: 0
    val responseData = networkResponse?.data?.let { String(it) } ?: "No response data"
    val errorTitle = scrapeErrorFromResponseData(responseData) ?: "No title"

    return statusCode to errorTitle
}

fun scrapeErrorFromResponseData(responseData: String?): String? {
    if (responseData != null) {
        val titleRegex = "<title>(.*?)</title>".toRegex(RegexOption.IGNORE_CASE)
        val matchResult = titleRegex.find(responseData)
        return matchResult?.groups?.get(1)?.value
    } else return null
}