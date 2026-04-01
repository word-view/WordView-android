package cc.wordview.app.components.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class BackTopAppBarTest : ComposeTest() {
    private val onClickBack = mock(Runnable::class.java)

    @Before
    fun setup() {
        composeTestRule.setContent {
            BackTopAppBar(
                title = { Text(text = "Test Title") },
                onClickBack = { onClickBack.run() }
            )
        }
    }

    @Test
    fun renders() {
        composeTestRule.onNodeWithText("Test Title")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("back-button")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun backButtonInvokesCallback() {
        composeTestRule.onNodeWithTag("back-button").performClick()
        verify(onClickBack).run()
    }
}