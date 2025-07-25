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

class TranslationsRequestTest : RequestTest() {
    @Test
    fun translationsJapanese() {
        getTranslations(
            lang = "ja",
            words = listOf("listen"),
            expectedTranslationSize = 1
        )
    }

    private fun getTranslations(lang: String, words: List<String>, expectedTranslationSize: Int) {
        val url = APIUrl("$endpoint/api/v1/lesson/translations")

        val array = JSONArray()

        for (word in words) array.put(word)

        val json = JSONObject()
            .put("lang", lang)
            .put("words", array)

        val request = TranslationsRequest(
            url.getURL(),
            json,
            { assert(it.size == expectedTranslationSize) },
            { throw FailedTestRequestException("Are you sure the API is running?") }
        )

        makeRequest(request)
    }
}