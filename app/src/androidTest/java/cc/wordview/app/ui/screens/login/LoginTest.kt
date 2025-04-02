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

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import cc.wordview.app.ui.activities.auth.AuthActivity
import cc.wordview.app.ui.activities.auth.viewmodel.LoginRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class LoginTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<AuthActivity>()

    @Inject
    lateinit var loginRepository: LoginRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun loginCorrect() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithTag("email-field").performTextInput("success@test.com")
        composeTestRule.onNodeWithText("Invalid email!").assertDoesNotExist()
    }

    @Test
    fun loginInvalidEmail() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithTag("email-field").performTextInput("success@testm")
        composeTestRule.onNodeWithText("Invalid email!").assertExists()
    }

    @Test
    fun navigateToRegister() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("auth-form"), 5_000)
        composeTestRule.onNodeWithText("Create an account").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasText("Register"), 5_000)
    }
}