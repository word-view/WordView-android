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

package cc.wordview.app.api

import cc.wordview.app.api.request.TranslationsRequest
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

class TranslationsRequestTest : RequestTest()  {
    @Test
    fun getTranslation_Portuguese() {
        getTranslation(
            lang = "pt",
            expectedSize = 1,
            words = listOf("run"),
        )
    }

    @Test
    fun getTranslation2Words_Portuguese() {
        getTranslation(
            lang = "pt",
            expectedSize = 1,
            words = listOf("run", "listen"),
        )
    }

    @Test
    fun getTranslation_English() {
        getTranslation(
            lang = "en",
            expectedSize = 1,
            words = listOf("run"),
        )
    }

    @Test
    fun getTranslation2Words_English() {
        getTranslation(
            lang = "en",
            expectedSize = 2,
            words = listOf("run", "listen"),
        )
    }

    @Test
    fun getTranslation_Japanese() {
        getTranslation(
            lang = "ja",
            expectedSize = 1,
            words = listOf("run"),
        )
    }

    @Test
    fun getTranslation2Words_Japanese() {
        getTranslation(
            lang = "ja",
            expectedSize = 2,
            words = listOf("run", "listen"),
        )
    }

    private fun getTranslation(lang: String, words: List<String>, expectedSize: Int) {
        val url = "$endpoint/api/v1/lesson/translations"

        val jsonArray = JSONArray()

        for (word in words)
            jsonArray.put(word)

        val json = JSONObject()
            .put("lang", lang)
            .put("words", jsonArray)

        val request = TranslationsRequest(
            url,
            json,
            {
                assert(it.size == expectedSize)
            },
            { throw FailedTestRequestException("Are you sure the API is running?") }
        )

        makeRequest(request)
    }
}