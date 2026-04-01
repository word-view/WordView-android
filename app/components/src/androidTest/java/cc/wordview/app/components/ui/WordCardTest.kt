package cc.wordview.app.components.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class WordCardTest : ComposeTest() {
    private val onClick = mock(Runnable::class.java)

    private fun setup(onClick: Runnable) {
        composeTestRule.setContent {
            WordCard(text = "Hello", onClick = { onClick.run() })
        }
    }

    @Test
    fun press() {
        setup(onClick = onClick)

        composeTestRule.onNodeWithText("Hello")
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        verify(onClick).run()
    }
}