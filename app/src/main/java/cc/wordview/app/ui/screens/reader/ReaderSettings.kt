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

package cc.wordview.app.ui.screens.reader

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.zhanghai.compose.preference.listPreference

@Composable
fun ReaderSettings() {
    LazyColumn(Modifier.fillMaxWidth()) {
        listPreference(
            key = "reader_theme",
            defaultValue = "LIGHT",
            values = listOf("LIGHT", "DARK", "SEPIA"),
            title = { Text(text = "Theme") },
            summary = { Text(text = it) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.ColorLens,
                    contentDescription = null
                )
            },
        )
    }
}