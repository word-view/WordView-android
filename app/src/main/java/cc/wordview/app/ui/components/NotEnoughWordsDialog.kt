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
import androidx.compose.ui.res.stringResource
import cc.wordview.app.R

@Preview
@Composable
fun NotEnoughWordsDialog(onConfirm: () -> Unit = {}) {
    AlertDialog(
        modifier = Modifier.testTag("not-enough-words-alert-dialog"),
        icon = {
            Icon(Icons.Filled.Translate, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.not_enough_words))
        },
        text = {
            Text(text = stringResource(R.string.there_were_not_enough_words_in_the_song_to_create_a_lesson))
        },
        onDismissRequest = { onConfirm() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(stringResource(R.string.go_back))
            }
        },
        dismissButton = {}
    )
}