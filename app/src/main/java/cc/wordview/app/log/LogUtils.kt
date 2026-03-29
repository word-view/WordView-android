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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File
import java.io.FileWriter

fun exportLogs(context: Context, logs: String) {
    try {
        if (logs.isBlank()) {
            Toast.makeText(context, "The log is currently empty!", Toast.LENGTH_SHORT).show()
            return
        }


        val logFile = File(context.filesDir, "wvapp_logs_${System.currentTimeMillis()}.txt")

        FileWriter(logFile).use { writer ->
            writer.write(logs)
        }

        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            logFile
        )

        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, "text/plain")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(viewIntent)
    } catch (e: Exception) {
        Timber.e(e, "Failed to export logs")
        Toast.makeText(context, "Unable to export logs due to an error", Toast.LENGTH_LONG).show()
    }
}