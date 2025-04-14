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

import cc.wordview.app.api.request.AuthRequest
import org.json.JSONObject
import org.junit.Test

class LoginRequestTest : RequestTest() {
    @Test
    fun login() {
        val json = JSONObject()
            .put("email", "mock.user@gmail.com")
            .put("password", "S_enha64")

        val request = AuthRequest(
            "$endpoint/api/v1/user/login",
            json,
            { assert(it.startsWith("ey")) },
            { _, _ -> throw FailedTestRequestException("Are you sure the API is running?") }
        )

        makeRequest(request)
    }

    @Test
    fun loginWrongPassword() {
        val json = JSONObject()
            .put("email", "mock.user@gmail.com")
            .put("password", "enha64")

        val request = AuthRequest(
            "$endpoint/api/v1/user/login",
            json,
            { },
            { status, message ->
                run {
                    assert(status == 401)
                    assert(message == "IncorrectCredentialsException: Bad credentials")
                }
            }
        )

        makeRequest(request)
    }

    @Test
    fun loginWrongEmail() {
        val json = JSONObject()
            .put("email", "mock.use@gmail.com")
            .put("password", "S_enha64")

        val request = AuthRequest(
            "$endpoint/api/v1/user/login",
            json,
            { },
            { status, message ->
                run {
                    assert(status == 404)
                    assert(message == "NoSuchEntryException: Unable to find a user with this email")
                }
            }
        )

        makeRequest(request)
    }
}