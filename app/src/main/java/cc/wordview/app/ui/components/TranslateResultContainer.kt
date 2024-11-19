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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.ui.theme.Typography
import cc.wordview.app.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslateResultContainer(correct: Boolean = true, words: List<String>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .padding(horizontal = 5.dp),
        color = if (correct) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.errorContainer,
        shape = DefaultRoundedCornerShape
    ) {
        Column {
            Row(
                Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 10.dp)
            ) {
                Icon(
                    imageVector = if (correct) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                    contentDescription = if (correct) "Correct icon" else "Incorrect icon"
                )
                Text(
                    text = if (correct) stringResource(R.string.correct_answer) else stringResource(
                        R.string.wrong_answer
                    ),
                    modifier = Modifier.padding(start = 10.dp),
                    style = Typography.titleLarge
                )
            }
            Text(
                text = if (correct) stringResource(R.string.you_answered_correctly_click_proceed_to_continue_the_lesson)
                else stringResource(R.string.you_wrongly_translated_the_phrase_the_correct_order_is),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 10.dp),
                style = Typography.bodyLarge
            )

            if (!correct) {
                FlowRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    words.forEachIndexed { _, word ->
                        Surface(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            shape = DefaultRoundedCornerShape
                        ) {
                            Text(
                                text = word,
                                modifier = Modifier.padding(
                                    horizontal = 15.dp,
                                    vertical = 10.dp
                                ),
                                style = Typography.titleLarge,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}