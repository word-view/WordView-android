package cc.wordview.app.components.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/**
 * A composable function that displays a top app bar with a back navigation icon.
 *
 * The [BackTopAppBar] composable creates a Material 3 [TopAppBar] with a customizable title and a back
 * button. The back button, represented by an [Icon] with an arrow, triggers the [onClickBack] callback
 * when clicked. The app bar's colors are set to match the background and content colors of the current
 * theme.
 *
 * @param title A composable function that defines the content to be displayed as the app bar's title.
 * @param onClickBack The callback invoked when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopAppBar(title: @Composable () -> Unit, onClickBack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = LocalContentColor.current
        ),
        title = {
            title()
        },
        navigationIcon = {
            IconButton(onClick = onClickBack, modifier = Modifier.testTag("back-button")) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        }
    )
}