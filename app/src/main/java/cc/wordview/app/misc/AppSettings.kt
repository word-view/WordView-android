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

package cc.wordview.app.misc

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.zhanghai.compose.preference.LocalPreferenceFlow
import me.zhanghai.compose.preference.Preferences

object AppSettings {
   val language = Setting(key = "language", defaultValue = "ja")
   val composerMode = Setting(key = "composer_mode", defaultValue = false)
}

class Setting<T>(val key: String, val defaultValue: T) {
   /**
    * Returns the saved setting value, if the value was not yet registered, this will
    * return the `defaultValue`
    */
   @SuppressLint("ComposableNaming")
   @Composable
   fun get(): T {
      val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()
      return get(preferences)
   }

   /**
    * Using the specified preferences instance, returns the saved
    * setting value, if the value was not yet registered, this will
    * return the `defaultValue`
    *
    * @param preferences The preferences that will be used
    */
   fun get(preferences: Preferences): T {
      return preferences.get<T>(key) ?: defaultValue
   }
}