package cc.wordview.app.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape

@Composable
fun WVButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .width(360.dp)
            .height(60.dp),
        shape = DefaultRoundedCornerShape
    ) {
        Text(text = text)
    }
}