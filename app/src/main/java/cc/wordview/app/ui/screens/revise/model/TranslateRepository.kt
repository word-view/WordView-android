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

package cc.wordview.app.ui.screens.revise.model

import android.util.Log
import cc.wordview.app.api.ApiRequestRepository
import cc.wordview.app.api.Response
import com.android.volley.DefaultRetryPolicy
import java.net.URLEncoder
import javax.inject.Inject

class TranslateRepository @Inject constructor() : ApiRequestRepository() {
    private val TAG = this::class.java.simpleName

    var onGetPhraseSuccess: (String) -> Unit = {}
    var onGetPhraseFail: () -> Unit = {}

    fun getPhrase(phraseLang: String, wordsLang: String, keyword: String) {
        val url =
            "http://$endpoint:8080/api/v1/lesson/phrase?phraseLang=$phraseLang&wordsLang=$wordsLang&keyword=${URLEncoder.encode(keyword)}"

        val response = Response({ onGetPhraseSuccess(it) },
            { Log.e(TAG, "getPhrase: ", it); onGetPhraseFail() })

        val request = jsonRequest(url, response)

        request.setRetryPolicy(
            DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        queue.add(request)
    }
}