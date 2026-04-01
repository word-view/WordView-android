package cc.wordview.app.components.ui

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule

open class ComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    val context: Context
        get() {
            lateinit var c: Context
            composeTestRule.setContent {
                c = LocalContext.current
            }
            return c
        }
}