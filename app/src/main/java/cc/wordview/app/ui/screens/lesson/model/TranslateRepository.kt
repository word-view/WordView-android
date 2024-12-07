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

import cc.wordview.app.api.APIUrl
import cc.wordview.app.api.ApiRequestRepository
import cc.wordview.app.api.Response
import cc.wordview.app.extensions.asURLEncoded
import com.android.volley.RequestQueue
import com.google.gson.Gson
import javax.inject.Inject

class TranslateRepository @Inject constructor() : ApiRequestRepository {
    var onGetPhraseSuccess: (Phrase) -> Unit = {}
    var onGetPhraseFail: () -> Unit = {}

    override lateinit var endpoint: String
    override lateinit var queue: RequestQueue

    fun getPhrase(phraseLang: String, wordsLang: String, keyword: String) {
        val url = APIUrl("$endpoint/api/v1/lesson/phrase")

        url.addRequestParam("phraseLang", phraseLang)
        url.addRequestParam("wordsLang", wordsLang)
        url.addRequestParam("keyword", keyword.asURLEncoded())

        val response = Response({
            val phrase = Gson().fromJson(it, Phrase::class.java)
            onGetPhraseSuccess(phrase)
        }, { onGetPhraseFail() })

        val request = jsonRequest(url.getURL(), response)

        request.setRetryPolicy(highTimeoutRetryPolicy)

        queue.add(request)
    }
}