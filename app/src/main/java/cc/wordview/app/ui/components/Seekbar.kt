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

import android.annotation.SuppressLint
import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.wordview.app.components.extensions.fillMaxWidth
import cc.wordview.app.components.extensions.percentageOf
import cc.wordview.app.ui.theme.Typography

@Composable
fun Seekbar(
    modifier: Modifier = Modifier,
    displayAdvancedInformation: Boolean = false,
    currentPosition: Long,
    duration: Long,
    videoId: String,
    @IntRange(from = 0, to = 100) bufferingProgress: Int,
) {
    val progress = duration.percentageOf(currentPosition)

    Column(modifier.testTag("seekbar")) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(2.5.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .alpha(0.75f)
        ) {
            Box(
                Modifier
                    .fillMaxWidth(bufferingProgress)
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colorScheme.outline)
                    .testTag("buffer-line")
            )

            Box(
                Modifier
                    .fillMaxWidth((progress / 100f).toFloat())
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .testTag("progress-line")
            )
        }
        Box(
            Modifier
                .padding(horizontal = 36.dp)
                .padding(top = 8.dp)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(4.dp).padding(horizontal = 8.dp),
                text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
                style = Typography.labelMedium,
                fontSize = 14.sp
            )
        }
        if (displayAdvancedInformation) {
            Box(
                Modifier
                    .padding(horizontal = 36.dp)
                    .padding(top = 8.dp)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(4.dp).padding(horizontal = 8.dp),
                    text = formatTimeComposerMode(currentPosition),
                    style = Typography.labelMedium,
                    fontSize = 14.sp
                )
            }
            Box(
                Modifier
                    .padding(horizontal = 36.dp)
                    .padding(top = 8.dp)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(4.dp).padding(horizontal = 8.dp),
                    text = "videoID=$videoId",
                    style = Typography.labelMedium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

fun formatTimeComposerMode(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val hours = (totalSeconds / 3600).toInt()
    val minutes = ((totalSeconds % 3600) / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()
    val millis = (milliseconds % 1000).toInt()

    return "%02d:%02d:%02d.%d".format(hours, minutes, seconds, millis)
}

@SuppressLint("DefaultLocale")
fun formatTime(milliseconds: Long): String {
    if (milliseconds <= 0) return "0:00"

    val minutes = (milliseconds / 1000) / 60
    val seconds = (milliseconds / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
