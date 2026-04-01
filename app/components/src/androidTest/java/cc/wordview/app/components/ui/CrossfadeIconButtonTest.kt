package cc.wordview.app.components.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class CrossfadeIconButtonTest : ComposeTest() {
    private fun setup(onClick: Runnable) {
        composeTestRule.setContent {
            var icon by mutableStateOf(Icons.Filled.PlayArrow)

            CrossfadeIconButton(
                modifier = Modifier.testTag("button"),
                icon = icon,
                size = 64.dp,
                onClick = {
                    icon =
                        if (icon == Icons.Filled.PlayArrow) Icons.Filled.ThumbUp else Icons.Filled.PlayArrow
                    onClick.run()
                })
        }
    }

    @Test
    fun sizeCorrect() {
        setup(mock(Runnable::class.java))
        composeTestRule.onNodeWithTag("button").assertHeightIsEqualTo(64.dp)
        composeTestRule.onNodeWithTag("button").assertWidthIsEqualTo(64.dp)
    }

    @Test
    fun press() {
        val onClick = mock(Runnable::class.java)
        setup(onClick)
        composeTestRule.onNodeWithTag("button").performClick()
        composeTestRule.onNodeWithTag("button").performClick()
        composeTestRule.onNodeWithTag("button").performClick()
        composeTestRule.onNodeWithTag("button").performClick()
        verify(onClick, times(4)).run()
    }
}