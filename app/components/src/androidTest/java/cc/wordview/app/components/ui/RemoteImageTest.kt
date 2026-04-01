package cc.wordview.app.components.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import cc.wordview.app.components.R
import org.junit.Ignore
import org.junit.Test

class RemoteImageTest : ComposeTest() {
    @Test
    @Ignore
    fun renders() {
        composeTestRule.setContent {
            RemoteImage(
                model = 0,
                asyncImagePlaceholders = AsyncImagePlaceholders(
                    noConnectionWhite = 0,
                    noConnectionDark = 0
                ),
            )
        }

        composeTestRule.onNodeWithTag("remote-image")
            .assertIsDisplayed()
            .assertExists()
    }
}