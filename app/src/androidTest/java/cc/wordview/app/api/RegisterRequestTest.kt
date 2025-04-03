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

import cc.wordview.app.api.request.LoginRequest
import cc.wordview.app.api.request.RegisterRequest
import org.json.JSONObject
import org.junit.Test
import java.util.UUID

class RegisterRequestTest : RequestTest() {
    @Test
    fun register() {
        val json = JSONObject()
            .put("username", "NewUser")
            .put("email", "${UUID.randomUUID()}@gmail.com")
            .put("password", "S_enha64")

        val request = RegisterRequest(
            "$endpoint/api/v1/user/register",
            json,
            { assert(it.startsWith("ey")) },
            { s, m -> throw FailedTestRequestException("Are you sure the API is running? $s, $m") }
        )

        makeRequest(request)
    }

    @Test
    fun registerEmailTaken() {
        val json = JSONObject()
            .put("username", "MockUser")
            .put("email", "mock.user@gmail.com")
            .put("password", "S_enha64")


        val request = LoginRequest(
            "$endpoint/api/v1/user/login",
            json,
            { },
            { status, message ->
                run {
                    assert(status == 401)
                    assert(message!!.startsWith("ValueTakenException"))
                }
            }
        )

        makeRequest(request)
    }

    @Test
    fun registerUsernameInvalid() {
        val json = JSONObject()
            .put("username", "Mock.User")
            .put("email", "${UUID.randomUUID()}@gmail.com")
            .put("password", "S_enha64")


        val request = LoginRequest(
            "$endpoint/api/v1/user/register",
            json,
            { },
            { status, message ->
                run {
                    assert(status == 400)
                    assert(message!!.startsWith("RequestValidationException"))
                }
            }
        )

        makeRequest(request)
    }

    @Test
    fun registerEmailInvalid() {
        val json = JSONObject()
            .put("username", "Mock.User")
            .put("email", "${UUID.randomUUID()}@com")
            .put("password", "S_enha64")


        val request = LoginRequest(
            "$endpoint/api/v1/user/register",
            json,
            { },
            { status, message ->
                run {
                    assert(status == 400)
                    assert(message!!.startsWith("RequestValidationException"))
                }
            }
        )

        makeRequest(request)
    }
}