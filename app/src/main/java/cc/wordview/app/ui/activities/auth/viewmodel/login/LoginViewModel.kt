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

package cc.wordview.app.ui.activities.auth.viewmodel.login

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.wordview.app.extensions.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)

    val isLoading = _isLoading.asStateFlow()

    fun login(email: String, password: String, context: Context) = viewModelScope.launch {
        _isLoading.update { true }

        loginRepository.apply {
            init(context)

            onSucceed = {
                Timber.e("Login succeeded! jwt=$it")
                context.showToast("Login succeeded!")

                _isLoading.update { false }
            }
            onFail = { s: String, i: Int ->
                Timber.e("Login failed \n\t message=$s, status=$i")

                if (s.startsWith("NoSuchEntryException")) {
                    context.showToast("This email address has not yet been registered")
                } else if (s.startsWith("IncorrectCredentialsException")) {
                    context.showToast("Incorrect credentials")
                }

                _isLoading.update { false }
            }

            login(email, password)
        }
    }
}