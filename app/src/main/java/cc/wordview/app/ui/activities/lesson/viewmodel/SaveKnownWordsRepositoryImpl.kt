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

package cc.wordview.app.ui.activities.lesson.viewmodel

import android.content.Context
import cc.wordview.app.api.APIUrl
import cc.wordview.app.api.request.AuthenticatedStringRequest
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class SaveKnownWordsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SaveKnownWordsRepository {
    override var onSucceed: (String) -> Unit = {}
    override var onFail: (String, Int) -> Unit = { message, status -> }

    override var queue = Volley.newRequestQueue(context)

    override fun saveKnownWords(lang: String, words: List<String>, jwt: String) {
        val url = APIUrl("$endpoint/api/v1/lesson/words/known")

        val jsonArray = JSONArray()

        for (word in words) {
            jsonArray.put(word)
        }

        val json = JSONObject()
            .put("language", lang)
            .put("words", jsonArray)

        val request = AuthenticatedStringRequest(
            url.getURL(),
            jwt,
            Request.Method.POST,
            json,
            { onSucceed(it) },
            { message, status -> onFail(message, status) }
        )

        queue.add(request)
    }
}