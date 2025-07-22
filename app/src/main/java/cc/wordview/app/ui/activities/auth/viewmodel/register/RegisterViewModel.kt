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

import android.content.Context
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
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _snackBarMessage = MutableSharedFlow<String>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    fun register(
        username: String,
        email: String,
        password: String,
        onRegisterCompleted: () -> Unit
    ) = viewModelScope.launch {
        _isLoading.update { true }

        registerRepository.apply {
            onSucceed = {
                Timber.e("Register succeeded! jwt=$it")
                _isLoading.update { false }
                setStoredJwt(it, appContext)
                onRegisterCompleted.invoke()
            }
            onFail = { s: String, i: Int ->
                Timber.e("Register failed \n\t message=$s, status=$i")

                if (s.contains("This email is already in use")) {
                    emitMessage(appContext.getString(R.string.this_email_is_already_in_use))
                } else if (s.contains("username cannot contain special characters")) {
                    emitMessage(appContext.getString(R.string.username_cannot_contain_special_characters))
                } else {
                    emitMessage(appContext.getString(R.string.could_not_connect_to_the_server))
                }

                _isLoading.update { false }
            }

            register(username, email, password)
        }
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _snackBarMessage.emit(msg)
        }
    }
}