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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.wordview.app.components.ui.Space
import cc.wordview.app.ui.theme.Typography

/**
 * A composable function that displays a placeholder-style home interface layout,
 * consisting of a header placeholder and a row of three placeholder cards.
 *
 * @param modifier The [Modifier] to be applied to the root container for layout customization.
 */
@Composable
fun MetaHomeInterface(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(start = 17.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(20.dp)
                ),
            text = "..............................",
            color = MaterialTheme.colorScheme.surfaceContainer,
            style = Typography.titleLarge,
        )

        Row(Modifier.padding(start = 17.dp, top = 17.dp)) {
            for (i in 1..3) {
                Column {
                    Box(
                        Modifier
                            .size(120.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(20.dp)
                            ),
                    )
                    Space(8.dp)
                    Text(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        text = "hello world",
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        style = Typography.labelMedium,
                    )
                    Space(4.dp)
                    Text(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        text = "hello world",
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        style = Typography.labelSmall,
                    )
                }
                Space(12.dp)
            }
        }
    }
}