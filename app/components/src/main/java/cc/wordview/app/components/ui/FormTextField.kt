package cc.wordview.app.components.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

/**
 * A composable function that displays a customizable outlined text field with optional error handling.
 *
 * It supports a leading icon, error state with a message, a label, and visual transformations for the input.
 * The error message is displayed conditionally using [AnimatedVisibility] when [isError] is true.
 *
 * @param modifier The [Modifier] to be applied to the text field for layout customization. Defaults to an empty [Modifier].
 * @param leadingIcon An optional composable function that defines the leading icon for the text field. Defaults to null.
 * @param value The current text value of the text field.
 * @param onValueChange The callback invoked when the text value changes, passing the new text value.
 * @param isError Whether the text field is in an error state. Defaults to false.
 * @param errorMessage The message to display when [isError] is true.
 * @param label An optional composable function that defines the label for the text field. Defaults to null.
 * @param visualTransformation The [VisualTransformation] to apply to the input text (e.g., for password masking). Defaults to [VisualTransformation.None].
 */
@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String,
    label: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        leadingIcon = leadingIcon,
        modifier = modifier,
        value = value,
        isError = isError,
        supportingText = {
            AnimatedVisibility(isError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        onValueChange = onValueChange,
        label = label,
        visualTransformation = visualTransformation
    )
}