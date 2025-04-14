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

package cc.wordview.app.ui.activities.auth.viewmodel.register

import cc.wordview.app.api.request.AuthRequest
import com.android.volley.RequestQueue
import org.json.JSONObject
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor() : RegisterRepository {
    override var onSucceed: (String) -> Unit = { _: String -> }
    override var onFail: (String, Int) -> Unit = { _: String, _: Int -> }

    override lateinit var queue: RequestQueue

    override fun register(username: String, email: String, password: String) {
        val json = JSONObject()
            .put("username", username)
            .put("email", email)
            .put("password", password)

        val request = AuthRequest(
            "$endpoint/api/v1/user/register",
            json,
            { onSucceed(it) },
            { status, message -> onFail(message ?: "", status ?: 0)  }
        )

        queue.add(request)
    }
}