package cc.wordview.app.components.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class OneTimeEffectTest : ComposeTest() {
    private val mockBlock = mock(Runnable::class.java)

    @Before
    fun setup() {
        composeTestRule.setContent {
            var topPadding by mutableStateOf(1)

            Column(Modifier.padding(start = topPadding.dp)) {
                OneTimeEffect { mockBlock.run() }

                Button(onClick = { topPadding += 10 }) {
                    Text("Recompose")
                }
            }
        }
    }

    @Test
    fun runsOnlyOnce() {
        verify(mockBlock, times(1)).run()
        composeTestRule.onNodeWithText("Recompose").performClick()
        verify(mockBlock, times(1)).run()
        composeTestRule.onNodeWithText("Recompose").performClick()
        verify(mockBlock, times(1)).run()
        composeTestRule.onNodeWithText("Recompose").performClick()
        verify(mockBlock, times(1)).run()
        composeTestRule.onNodeWithText("Recompose").performClick()
        verify(mockBlock, times(1)).run()
        composeTestRule.onNodeWithText("Recompose").performClick()
        verify(mockBlock, times(1)).run()
    }
}