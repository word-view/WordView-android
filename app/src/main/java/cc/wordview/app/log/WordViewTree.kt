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

package cc.wordview.app.log

import android.util.Log
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WordViewTree : Timber.Tree() {
    private val logBuffer: StringBuffer = StringBuffer()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val timestamp = getCurrentTimestamp()
        var fullMessage = "$timestamp ${tag ?: ""} ${priorityToString(priority)}: $message\n"
        if (t != null) {
            fullMessage = fullMessage.plus(Log.getStackTraceString(t) + "\n")
        }
        logBuffer.append(fullMessage)
    }

    private fun priorityToString(priority: Int): String = when (priority) {
        Log.VERBOSE -> "V"
        Log.DEBUG -> "D"
        Log.INFO -> "I"
        Log.WARN -> "W"
        Log.ERROR -> "E"
        Log.ASSERT -> "A"
        else -> "?"
    }

    fun getLogs(): String = logBuffer.toString()

    private fun getCurrentTimestamp(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return LocalDateTime.now().format(formatter)
    }
}