package cc.wordview.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cc.wordview.app.R
import cc.wordview.app.ui.components.WordViewButton
import cc.wordview.app.ui.theme.Typography
import cc.wordview.app.ui.theme.poppinsFamily

@Composable
fun Welcome(navController: NavHostController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.wvicon),
                    contentDescription = "WordView icon"
                )
                Text(
                    text = "WordView",
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = poppinsFamily,
                    fontSize = 42.sp
                )
                Text(
                    text = "The smart way to learn\na language",
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.size(120.dp))
                WordViewButton(
                    "Start learning",
                    onClick = { navController.navigate("register") })
                Spacer(Modifier.size(10.dp))
                WordViewButton(
                    "I already have an account",
                    onClick = { print("hello") },
                )
            }
        }
    }
}