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

package cc.wordview.app

import android.app.Application
import android.content.Intent
import cc.wordview.app.log.WordViewTree
import cc.wordview.app.ui.activities.crash.CrashActivity
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import kotlin.system.exitProcess

@HiltAndroidApp
class WordViewApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleUncaughtException(thread, throwable)
            oldHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun handleUncaughtException(thread: Thread, throwable: Throwable) {
        Timber.e(throwable, "An unhandled exception has happened on $thread")
        val logs = (Timber.forest().firstOrNull { it is WordViewTree } as? WordViewTree)?.getLogs()
            ?: "No logs available"

        val intent = Intent(applicationContext, CrashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("LOGS", logs)
        }

        applicationContext.startActivity(intent)

        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }
}