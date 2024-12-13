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

package cc.wordview.app.api.request

import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.google.gson.JsonParser

class LyricsRequest(
    url: String?,
    onSuccess: (String, String) -> Unit,
    onError: (String) -> Unit,
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
        onError(it.message ?: "Request failed with status code $statusCode\n$errorTitle")
    }) {

    init {
        retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    override fun getHeaders(): MutableMap<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Accept"] = "application/json;charset=utf-8"
        return headers
    }

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