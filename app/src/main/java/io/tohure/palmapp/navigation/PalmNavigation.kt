package io.tohure.palmapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.tohure.palmapp.ChatScreen
import io.tohure.palmapp.MainScreen
import io.tohure.palmapp.TextScreen

@Composable
fun PalmNavigation() {

    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = PalmScreens.MainScreen.route) {
        composable(route = PalmScreens.MainScreen.route) {
            MainScreen(navController)
        }
        composable(route = PalmScreens.TextScreen.route) {
            TextScreen()
        }
        composable(route = PalmScreens.ChatScreen.route) {
            ChatScreen()
        }
    }
}