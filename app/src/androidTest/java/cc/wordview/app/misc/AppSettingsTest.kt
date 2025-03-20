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

@file:Suppress("UNUSED_VARIABLE")

package cc.wordview.app.misc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.ComposeTest
import me.zhanghai.compose.preference.LocalPreferenceFlow
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.junit.Test

class AppSettingsTest : ComposeTest() {
    private fun setup(block: @Composable () -> Unit) {
        composeTestRule.setContent {
            ProvidePreferenceLocals {
                block()
            }
        }
    }

    @Test
    fun getLanguage() {
        setup {
            val langTag = AppSettings.language.get()
            assert(langTag.length == 2)
        }
    }

    @Test
    fun getLanguagePassingPreferences() {
        setup {
            val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()
            val langTag = AppSettings.language.get(preferences)
            assert(langTag.length == 2)
        }
    }

    @Test
    fun getComposerMode() {
        setup {
            val composerMode = AppSettings.composerMode.get()
        }
    }

    @Test
    fun getComposerModePassingPreferences() {
        setup {
            val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()
            val composerMode = AppSettings.composerMode.get(preferences)
        }
    }
}