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

package cc.wordview.app.ui.activities.home.composables.home.tabs

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import cc.wordview.app.R

data class BottomNavigationItem(
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = "",
    val name : String = "",
) {
    fun bottomNavigationItems(context: Context) : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                icon = Icons.Filled.MusicNote,
                route = Tabs.Music.route,
                name = context.getString(R.string.music)
            ),
            BottomNavigationItem(
                icon = Icons.Filled.VideoLibrary,
                route = Tabs.Video.route,
                name = context.getString(R.string.video),
            ),
        )
    }
}