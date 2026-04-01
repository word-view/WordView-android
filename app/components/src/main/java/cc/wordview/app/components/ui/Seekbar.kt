package cc.wordview.app.components.ui

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
import androidx.compose.material3.MaterialTheme.typography
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

/**
 * A composable function that displays a seek bar for media playback with progress, buffering, and time information.
 *
 * The [Seekbar] composable creates a visual representation of a media player's progress, showing a progress bar,
 * a buffering bar, and the current and total duration of the media. Optionally, it can display advanced information
 * such as the exact time in milliseconds and the video ID.
 *
 * @param modifier The [Modifier] to be applied to the seek bar for layout customization. Defaults to an empty [Modifier].
 * @param displayAdvancedInformation Whether to display additional information like composer mode time and video ID. Defaults to false.
 * @param currentPosition The current position of the media in milliseconds.
 * @param duration The total duration of the media in milliseconds.
 * @param videoId The unique identifier of the video being played.
 * @param bufferingProgress The buffering progress as a percentage, ranging from 0 to 100.
 */
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
                style = typography.labelMedium,
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
                    style = typography.labelMedium,
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
                    style = typography.labelMedium,
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
