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

package cc.wordview.app.ui.activities.auth.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.components.extensions.openActivity
import cc.wordview.app.components.ui.CircularProgressIndicator
import cc.wordview.app.components.ui.FormTextField
import cc.wordview.app.components.ui.Icon
import cc.wordview.app.components.ui.Space
import cc.wordview.app.ui.activities.auth.composables.FormValidation.Email
import cc.wordview.app.ui.activities.auth.composables.FormValidation.Password
import cc.wordview.app.ui.activities.auth.viewmodel.register.RegisterViewModel
import cc.wordview.app.ui.activities.home.HomeActivity
import cc.wordview.app.ui.components.AuthForm
import kotlinx.coroutines.launch

@Composable
@Preview
fun Register(
    navController: NavHostController = rememberNavController(),
    viewModel: RegisterViewModel = hiltViewModel()
) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repeat by rememberSaveable { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val message by viewModel.snackBarMessage.collectAsState(initial = "")

    LaunchedEffect(Unit) {
        viewModel.snackBarMessage.collect {
            scope.launch {
                snackBarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            AuthForm(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.Center),
                title = "Register"
            ) {
                FormTextField(
                    leadingIcon = { Icon(Icons.Filled.AccountBox) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username-field"),
                    value = username,
                    isError = false,
                    errorMessage = "Invalid username!",
                    onValueChange = { username = it.take(20) },
                    label = { Text("Username") }
                )
                Space(12.dp)
                FormTextField(
                    leadingIcon = { Icon(Icons.Filled.Mail) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email-field"),
                    value = email,
                    isError = email.isNotEmpty() && !Email.validate(email),
                    errorMessage = "Invalid email!",
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                Space(12.dp)
                FormTextField(
                    leadingIcon = { Icon(Icons.Filled.Password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password-field"),
                    value = password,
                    isError = password.isNotEmpty() && !Password.validate(password),
                    errorMessage = "Password is too weak",
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text("Password") }
                )
                Space(12.dp)
                FormTextField(
                    leadingIcon = { Icon(Icons.Filled.Password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("repeat-field"),
                    value = repeat,
                    isError = password.isNotEmpty() && repeat.isNotEmpty() && repeat != password,
                    errorMessage = "Passwords are not equal!",
                    onValueChange = { repeat = it },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text("Repeat Password") }
                )
                Space(24.dp)
                Button(
                    modifier = Modifier.fillMaxWidth(.9f),
                    enabled =
                        username.isNotEmpty() &&
                                email.isNotEmpty() && Email.validate(email) &&
                                password.isNotEmpty() && Password.validate(password) &&
                                repeat == password,
                    onClick = {
                        if (!isLoading) viewModel.register(
                            username = username,
                            email = email,
                            password = password,
                            onRegisterCompleted = { context.openActivity<HomeActivity>() }
                        )
                    }
                ) {
                    if (!isLoading) {
                        Text("Create")
                    } else {
                        CircularProgressIndicator(12.dp)
                    }
                }
                Space(12.dp)
                Text("Or")
                Space(12.dp)
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(.5f),
                    onClick = { navController.navigate("login") }
                ) {
                    Text("Log in")
                }
            }
        }
    }
}