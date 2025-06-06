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

package cc.wordview.app.api

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit
import java.io.IOException
import javax.crypto.AEADBadTagException

private const val PREF_NAME = "user_store"
private const val TOKEN_KEY = "jwt_token"

private fun getMasterKey(context: Context): MasterKey =
    MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

private fun getEncryptedPrefs(context: Context) =
    EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        getMasterKey(context),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

fun getStoredJwt(context: Context): String? {
    return try {
        val sharedPreferences = getEncryptedPrefs(context)
        sharedPreferences.getString(TOKEN_KEY, null)
    } catch (e: AEADBadTagException) {
        context.deleteSharedPreferences(PREF_NAME)
        null
    } catch (e: IOException) {
        context.deleteSharedPreferences(PREF_NAME)
        null
    }
}

@Composable
fun getStoredJwt(): String? {
    val context = LocalContext.current

    return try {
        val sharedPreferences = getEncryptedPrefs(context)
        sharedPreferences.getString(TOKEN_KEY, null)
    } catch (e: AEADBadTagException) {
        context.deleteSharedPreferences(PREF_NAME)
        null
    } catch (e: IOException) {
        context.deleteSharedPreferences(PREF_NAME)
        null
    }
}

fun setStoredJwt(token: String, context: Context) {
    val sharedPreferences = getEncryptedPrefs(context)
    sharedPreferences.edit(commit = true) {
        putString(TOKEN_KEY, token)
    }
}