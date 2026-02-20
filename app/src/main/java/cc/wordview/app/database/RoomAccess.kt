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

package cc.wordview.app.database

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.room.Room
import cc.wordview.app.BuildConfig
import timber.log.Timber

object RoomAccess {
    private lateinit var database: WordViewDatabase

    @SuppressLint("RestrictedApi", "LogNotTimber")
    fun open(context: Context) {
        database = Room.databaseBuilder(
            context,
            WordViewDatabase::class.java,
            "app-${BuildConfig.BUILD_TYPE}"
        ).build()

        Log.i("RoomAccess", "Opened database at ${database.path}")
    }

    fun getDatabase(): WordViewDatabase {
        return database
    }
}