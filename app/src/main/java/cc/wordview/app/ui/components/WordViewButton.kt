package cc.wordview.app.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WordViewButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .width(360.dp)
            .height(60.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text = text)
    }
}