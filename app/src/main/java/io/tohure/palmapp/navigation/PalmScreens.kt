package io.tohure.palmapp.navigation

sealed class PalmScreens(val route: String) {
    object MainScreen: PalmScreens("main_screen")
    object TextScreen: PalmScreens("text_screen")
    object ChatScreen: PalmScreens("chat_screen")
}