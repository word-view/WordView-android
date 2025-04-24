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

package cc.wordview.app.ui.screens.register

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import cc.wordview.app.ui.activities.auth.AuthActivity
import cc.wordview.app.ui.activities.auth.viewmodel.register.RegisterRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class RegisterTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<AuthActivity>()

    @Inject
    lateinit var registerRepository: RegisterRepository

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.onNodeWithText("Create an account").performClick()
    }

    @Test
    fun emailCorrect() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithTag("email-field").performTextInput("success@test.com")
        composeTestRule.onNodeWithText("Invalid email!").assertDoesNotExist()
    }

    @Test
    fun emailInvalid() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithTag("email-field").performTextInput("success@testm")
        composeTestRule.onNodeWithText("Invalid email!").assertExists()
    }

    @Test
    fun navigateToLogin() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithText("Log in").performClick()
    }

    @Test
    fun passwordNotWeak() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithTag("password-field").performTextInput("123456")
        composeTestRule.onNodeWithText("Password is too weak").assertDoesNotExist()
        composeTestRule.onNodeWithText("Create").assertIsNotEnabled()
    }

    @Test
    fun passwordWeak() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithTag("password-field").performTextInput("123")
        composeTestRule.onNodeWithText("Password is too weak").assertExists()
        composeTestRule.onNodeWithText("Create").assertIsNotEnabled()
    }

    @Test
    fun passwordsEqual() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithTag("password-field").performTextInput("123456")
        composeTestRule.onNodeWithTag("repeat-field").performTextInput("123456")
        composeTestRule.onNodeWithText("Passwords are not equal!").assertDoesNotExist()
        composeTestRule.onNodeWithText("Create").assertIsNotEnabled()
    }

    @Test
    fun passwordsDifferent() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithTag("password-field").performTextInput("123456")
        composeTestRule.onNodeWithTag("repeat-field").performTextInput("123")
        composeTestRule.onNodeWithText("Passwords are not equal!").assertExists()
        composeTestRule.onNodeWithText("Create").assertIsNotEnabled()
    }

    @Test
    fun makeRegistration() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithTag("username-field").performTextInput("MockUser")
        composeTestRule.onNodeWithTag("email-field").performTextInput("success@test.com")
        composeTestRule.onNodeWithTag("password-field").performTextInput("123456")
        composeTestRule.onNodeWithTag("repeat-field").performTextInput("123456")
        composeTestRule.onNodeWithText("Create")
            .assertIsEnabled()
            .performClick()

        composeTestRule.onNodeWithText("Register failed!").assertDoesNotExist()
    }

    @Test
    fun makeRegistration_ExistingEmail() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithTag("username-field").performTextInput("MockUser")
        composeTestRule.onNodeWithTag("email-field").performTextInput("existing.email@test.com")
        composeTestRule.onNodeWithTag("password-field").performTextInput("123456")
        composeTestRule.onNodeWithTag("repeat-field").performTextInput("123456")
        composeTestRule.onNodeWithText("Create")
            .assertIsEnabled()
            .performClick()

        composeTestRule.onNodeWithText("Register failed!").assertExists()
    }
}