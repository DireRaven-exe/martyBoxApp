package com.jetbrains.kmpapp.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector?
) {

    object Home : NavigationItem("/home_screen", "Home", null)
    object QRCode : NavigationItem("/qr_code_screen", "QR", null)
    object Main : NavigationItem("/main_screen", "Main", null)
    object Demo : NavigationItem("/demo_screen", "Main", null)
}


//internal val home_route = "/home_screen"
//internal val qr_route = home_route + "/qr_code_screen"
//internal val main_from_home_route = home_route + "/main_screen"
