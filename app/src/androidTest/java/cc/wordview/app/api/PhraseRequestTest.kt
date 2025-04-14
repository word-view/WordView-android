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

import cc.wordview.app.api.request.PhraseRequest
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

class PhraseRequestTest : RequestTest()  {
    @Test
    fun getPhrase_English() {
        getPhrase(
            words = listOf("sky"),
            phraseLang = "en",
            wordsLang = "en",
            expectedPhrase = "The sky is blue"
        )
    }

    @Test
    fun getPhrase_Portuguese() {
        getPhrase(
            words = listOf("céu"),
            phraseLang = "pt",
            wordsLang = "pt",
            expectedPhrase = "O céu e azul"
        )
    }

    private fun getPhrase(words: List<String>, phraseLang: String, wordsLang: String, expectedPhrase: String) {
        val url = "$endpoint/api/v1/lesson/phrase"

        val jsonArray = JSONArray()

        for (word in words)
            jsonArray.put(word)

        val json = JSONObject()
            .put("phraseLang", phraseLang)
            .put("wordsLang", wordsLang)
            .put("keywords", jsonArray)

        val request = PhraseRequest(
            url,
            json,
            { assert(it.first().phrase == expectedPhrase) },
            { throw FailedTestRequestException("Are you sure the API is running?") }
        )

        makeRequest(request)
    }
}