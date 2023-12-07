package io.tohure.palmapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.tohure.palmapp.navigation.PalmScreens

@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PaLM Api Example",
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(23.dp))
        Button(
            onClick = { navController.navigate(PalmScreens.TextScreen.route) },
        ) {
            Text("Text Demo")
        }

        Spacer(modifier = Modifier.height(23.dp))

        Button(
            onClick = { navController.navigate(PalmScreens.ChatScreen.route) },
        ) {
            Text("Chat Demo")
        }
    }
}
