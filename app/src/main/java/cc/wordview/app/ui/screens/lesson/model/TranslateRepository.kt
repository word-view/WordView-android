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

package cc.wordview.app.ui.screens.lesson.model

import cc.wordview.app.api.ApiRequestRepository
import cc.wordview.app.api.Response
import com.android.volley.RequestQueue
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject


class TranslateRepository @Inject constructor() : ApiRequestRepository {
    var onGetPhraseSuccess: (List<Phrase>) -> Unit = {}
    var onGetPhraseFail: () -> Unit = {}

    override lateinit var queue: RequestQueue

    fun getPhrase(phraseLang: String, wordsLang: String, keywords: List<String>) {
        val url = "$endpoint/api/v1/lesson/phrase"

        val response = Response({
            val phrases = ArrayList<Phrase>()

            val jsonObject = JsonParser.parseString(it).asJsonObject
            jsonObject.getAsJsonArray("phrases")
                .forEach { e -> phrases.add(Gson().fromJson(e.asString, Phrase::class.java)) }

            onGetPhraseSuccess(phrases)
        }, { onGetPhraseFail() })

        val json = JSONObject()

        json.put("phraseLang", phraseLang)
        json.put("wordsLang", wordsLang)

        val jsonArray = JSONArray()
        for (word in keywords) jsonArray.put(word)

        json.put("keywords", jsonArray)

        val request = jsonPostRequest(url, json, response)

        request.setRetryPolicy(highTimeoutRetryPolicy)

        queue.add(request)
    }
}