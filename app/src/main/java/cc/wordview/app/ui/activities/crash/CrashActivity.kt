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

package cc.wordview.app.ui.activities.crash

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cc.wordview.app.ui.activities.WordViewActivity
import cc.wordview.app.ui.theme.WordViewTheme
import timber.log.Timber
import kotlin.system.exitProcess

class CrashActivity : WordViewActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val logs = intent.getStringExtra("LOGS") ?: ""

        enableEdgeToEdge()
        setContent {
            WordViewTheme {
                CrashScreen(logs) { restartApplication() }
            }
        }
    }

    private fun restartApplication() {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )

            startActivity(intent)

            finishAffinity()
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)

        } catch (e: Exception) {
            Timber.e(e, "Failed to restart app")

            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(10)
        }
    }
}