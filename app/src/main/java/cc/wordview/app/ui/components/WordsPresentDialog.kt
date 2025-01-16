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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.languages.Word
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WordsPresentDialog(onConfirm: () -> Unit = {}, words: List<Word>) {
    var loading by rememberSaveable { mutableStateOf(true) }

    OneTimeEffect {
        CoroutineScope(Dispatchers.Main).launch {
            // TODO: fix this loading after resolving https://github.com/word-view/WordView-android/issues/30
            delay(550L)
            loading = false
        }
    }

    AlertDialog(
        modifier = Modifier.testTag("words-present-dialog"),
        icon = {
            Icon(Icons.Filled.FormatListNumbered, contentDescription = null)
        },
        title = {
            Text(text = "Words present in this song")
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (loading)
                    CircularProgressIndicator(24.dp)
                else for (wordToken in words) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            modifier = Modifier
                                .size(40.dp)
                                .aspectRatio(1f),
                            model = GlobalImageLoader.getCachedImage(wordToken.parent),
                            contentDescription = null
                        )
                        Spacer(Modifier.size(12.dp))
                        Column {
                            Text(
                                text = wordToken.word,
                                style = Typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            // TODO: use the translation to the user's language instead
                            Text(text = wordToken.parent, style = Typography.labelSmall)
                        }
                    }
                }
            }
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = { onConfirm() }) { Text("Proceed") }
        },
        dismissButton = {}
    )
}