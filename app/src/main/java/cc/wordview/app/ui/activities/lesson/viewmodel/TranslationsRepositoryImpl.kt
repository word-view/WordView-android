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

import cc.wordview.app.api.APIUrl
import cc.wordview.app.api.entity.Translation
import cc.wordview.app.api.request.TranslationsRequest
import com.android.volley.RequestQueue
import org.json.JSONArray
import org.json.JSONObject

class TranslationsRepositoryImpl : TranslationsRepository {
    override var onSucceed: (List<Translation>) -> Unit = {}
    override var onFail: (String, Int) -> Unit = { message, status -> }

    override lateinit var queue: RequestQueue

    override fun getTranslations(lang: String, words: List<String>) {
        val url = APIUrl("$endpoint/api/v1/lesson/translations")

        val array = JSONArray()

        for (word in words) array.put(word)

        val json = JSONObject()
            .put("lang", lang)
            .put("words", array)

        val request = TranslationsRequest(
            url.getURL(),
            json,
            { onSucceed(it) },
            { onFail("", 0) }
        )

        queue.add(request)
    }
}