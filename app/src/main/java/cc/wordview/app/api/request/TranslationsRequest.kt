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

import cc.wordview.app.api.entity.Translation
import cc.wordview.app.api.wordViewRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONObject
import timber.log.Timber

class TranslationsRequest(
    url: String,
    body: JSONObject,
    onSuccess: (List<Translation>) -> Unit,
    onError: () -> Unit,
) : JsonObjectRequest(Method.POST, url, body, {
    val translations = ArrayList<Translation>()

    val json = JsonParser.parseString(it.toString()).asJsonObject
    json.getAsJsonArray("translations")
        .forEach { e -> translations.add(Gson().fromJson(e, Translation::class.java)) }

    onSuccess(translations)
}, { onError() }) {
    init {
        Timber.v("init: method=POST, url=$url")

        retryPolicy = wordViewRetryPolicy
    }

    override fun getHeaders(): MutableMap<String, String> = mutableMapOf(
        "Content-Encoding" to "gzip"
    )
}