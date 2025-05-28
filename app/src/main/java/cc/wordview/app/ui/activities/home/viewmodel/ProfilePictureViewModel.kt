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

package cc.wordview.app.ui.activities.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import cc.wordview.app.BuildConfig
import cc.wordview.app.api.entity.User
import cc.wordview.app.api.request.MeRequest
import com.android.volley.toolbox.Volley
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfilePictureViewModel @Inject constructor() : ViewModel() {
    private val _user = MutableStateFlow(User("-1", " ", "", ""))

    val user = _user.asStateFlow()

    fun makeMeRequest(jwt: String?, context: Context) {
        if (jwt == null) return

        val endpoint = BuildConfig.API_BASE_URL
        val request = MeRequest(
            "$endpoint/api/v1/user/me",
            { user -> _user.update { user } },
            { msg, status -> Timber.e(msg) },
            jwt
        )

        Volley.newRequestQueue(context).add(request)
    }
}