/*
 * Copyright (c) 2024 Arthur Araujo
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

package cc.wordview.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Preview
@Composable
fun NotEnoughWordsDialog(onConfirm: () -> Unit = {}) {
    AlertDialog(
        modifier = Modifier.testTag("not-enough-words-alert-dialog"),
        icon = {
            Icon(Icons.Filled.Translate, contentDescription = null)
        },
        title = {
            Text(text = "Not enough words")
        },
        text = {
            Text(text = "There were not enough words in the song to create a lesson.")
        },
        onDismissRequest = { onConfirm() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("Go back")
            }
        },
        dismissButton = {}
    )
}