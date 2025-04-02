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

package cc.wordview.app.ui.screens.login

import cc.wordview.app.ui.activities.auth.viewmodel.LoginRepository
import com.android.volley.RequestQueue
import javax.inject.Inject

class MockLoginRepositoryImpl @Inject constructor() : LoginRepository {
    override var onSucceed: (String) -> Unit = {}
    override var onFail: (String, Int) -> Unit = { _: String, _: Int -> }

    override var endpoint: String = ""
    override lateinit var queue: RequestQueue

    override fun login(email: String, password: String) {
        when (email) {
            "success@test.com" -> onSucceed("eyJ...")
            "inexistent.email@test.com" -> onFail("NoSuchEntryException", 404)
            "incorrect.credentials@test.com" -> onFail("IncorrectCredentialsException", 401)
        }
    }
}