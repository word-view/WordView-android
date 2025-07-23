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
import cc.wordview.app.R
import cc.wordview.app.api.setStoredJwt
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _snackBarMessage = MutableSharedFlow<String>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    fun login(email: String, password: String, onLoginCompleted: () -> Unit) =
        viewModelScope.launch {
            _isLoading.update { true }

            loginRepository.apply {
                onSucceed = {
                    Timber.e("Login succeeded! jwt=$it")
                    setStoredJwt(it, appContext)
                    _isLoading.update { false }
                    onLoginCompleted.invoke()
                }
                onFail = { s: String, i: Int ->
                    Timber.e("Login failed \n\t message=$s, status=$i")

                    if (s.startsWith("NoSuchEntryException")) {
                        emitMessage(appContext.getString(R.string.this_email_address_has_not_yet_been_registered))
                    } else if (s.startsWith("IncorrectCredentialsException")) {
                        emitMessage(appContext.getString(R.string.incorrect_credentials))
                    } else {
                        emitMessage(appContext.getString(R.string.could_not_connect_to_the_server))
                    }

                    _isLoading.update { false }
                }

                login(email, password)
            }
        }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _snackBarMessage.emit(msg)
        }
    }
}