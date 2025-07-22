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

import cc.wordview.app.api.wordViewRetryPolicy
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.google.gson.JsonParser
import timber.log.Timber

class LyricsRequest(
    url: String,
    onSuccess: (String, String) -> Unit,
    onError: (String, Int) -> Unit,
) : StringRequest(
    Method.GET,
    url,
    {
        val (lyrics, dictionary) = parseLyricsAndDictionary(it)
        onSuccess(lyrics, dictionary)
    },
    {
        val statusCode = it.networkResponse?.statusCode
        val responseData = it.networkResponse?.data?.let { String(it) }
        val errorTitle = scrapeErrorFromResponseData(responseData)

        onError(it.message ?: "Request failed with status code $statusCode\n$errorTitle", statusCode ?: 0)
    }) {

    init {
        Timber.v("init: method=GET, url=$url")

        retryPolicy = wordViewRetryPolicy
    }

    override fun getHeaders(): MutableMap<String, String> = mutableMapOf(
        "Accept" to "application/json;charset=utf-8"
    )

    companion object {
        fun parseLyricsAndDictionary(res: String): Pair<String, String> {
            val jsonObject = JsonParser.parseString(res).asJsonObject

            val lyrics = jsonObject.get("lyrics").asString
            val dictionary = jsonObject.getAsJsonArray("dictionary").toString()

            return lyrics to dictionary
        }

        private fun scrapeErrorFromResponseData(responseData: String?): String? {
            if (responseData != null) {
                val titleRegex = "<title>(.*?)</title>".toRegex(RegexOption.IGNORE_CASE)
                val matchResult = titleRegex.find(responseData)
                return matchResult?.groups?.get(1)?.value
            } else return null
        }
    }
}