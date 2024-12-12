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

import cc.wordview.app.ui.screens.lesson.model.Phrase
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONObject

class PhraseRequest(
    url: String?,
    body: JSONObject,
    onSuccess: (List<Phrase>) -> Unit,
    onError: () -> Unit,
) : JsonObjectRequest(Method.POST, url, body, {
    val phrases = ArrayList<Phrase>()

    val json = JsonParser.parseString(it.toString()).asJsonObject
    json.getAsJsonArray("phrases")
        .forEach { e -> phrases.add(Gson().fromJson(e.asString, Phrase::class.java)) }

    onSuccess(phrases)
}, { onError() }) {
    init {
        retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }
}