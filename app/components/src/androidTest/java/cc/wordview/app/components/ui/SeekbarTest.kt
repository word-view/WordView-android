package cc.wordview.app.components.ui
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import org.junit.Test

class SeekbarTest : ComposeTest() {
    private fun setup(currentPosition: Long, duration: Long, bufferingProgress: Int) {
        composeTestRule.setContent {
            Seekbar(
                currentPosition = currentPosition,
                duration = duration,
                videoId = "",
                bufferingProgress = bufferingProgress
            )
        }
    }

    @Test
    fun positionsZero() {
        setup(0, 0, 0)
        composeTestRule.onNodeWithText("0:00 / 0:00").assertExists()
    }

    @Test
    fun progress0() {
        setup(0, 300_000, 50)
        composeTestRule.onNodeWithText("0:00 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(0.dp)
    }

    @Test
    fun progressNegative() {
        setup(-2500, 300_000, 50)
        composeTestRule.onNodeWithText("0:00 / 05:00").assertExists()
        composeTestRule.onNodeWithTag("progress-line").assertWidthIsEqualTo(0.dp)
    }

    @Test
    fun bufferAt0() {
        setup(0, 0, 0)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(0.dp)
    }

    @Test
    fun bufferNegative() {
        setup(0, 0, -10)
        composeTestRule.onNodeWithTag("buffer-line").assertWidthIsEqualTo(0.dp)
    }
}