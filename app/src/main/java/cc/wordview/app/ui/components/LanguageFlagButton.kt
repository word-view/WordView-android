package cc.wordview.app.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape

@Composable
fun LanguageFlagButton(languageName: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(150.dp),
        shape = DefaultRoundedCornerShape
    ) {
        Text(text = languageName)
    }
}