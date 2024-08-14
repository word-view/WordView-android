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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cc.wordview.app.R
import cc.wordview.app.api.VideoSearchResult
import cc.wordview.app.ui.theme.Typography
import coil.compose.AsyncImage
import kotlin.time.Duration.Companion.seconds

@Composable
fun ResultItem(result: VideoSearchResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        onClick = onClick
    ) {
        Row {
            Surface(
                modifier = Modifier
                    .size(72.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                AsyncImage(
                    model = "https://img.youtube.com/vi/${result.id}/0.jpg",
                    placeholder = painterResource(id = R.drawable.radio),
                    error = painterResource(id = R.drawable.radio),
                    contentDescription = "${result.title} cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = result.title,
                    style = Typography.labelMedium,
                    textAlign = TextAlign.Left,
                    softWrap = false,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = result.channel,
                    style = Typography.labelSmall,
                    textAlign = TextAlign.Left,
                    softWrap = false,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
                Spacer(Modifier.size(3.dp))
                Text(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer,
                            RoundedCornerShape(5.dp)
                        )
                        .padding(3.dp),
                    text = "${result.duration.seconds}",
                    style = Typography.labelSmall
                )
            }
        }
    }
}